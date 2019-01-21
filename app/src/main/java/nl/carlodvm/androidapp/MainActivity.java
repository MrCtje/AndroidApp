package nl.carlodvm.androidapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import nl.carlodvm.androidapp.Animation.ScalingNode;
import nl.carlodvm.androidapp.Core.Destination;
import nl.carlodvm.androidapp.Core.DestinationBitmapReader;
import nl.carlodvm.androidapp.Core.Grid;
import nl.carlodvm.androidapp.Core.MapReader;
import nl.carlodvm.androidapp.Core.PathFinder;
import nl.carlodvm.androidapp.Core.SensorManager;
import nl.carlodvm.androidapp.Core.World;
import nl.carlodvm.androidapp.DataTransferObject.ItemData;
import nl.carlodvm.androidapp.View.ImageSpinnerAdapter;
import nl.carlodvm.androidapp.View.MapView;
import nl.carlodvm.androidapp.View.PathView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private AugmentedImageFragment arFragment;
    private ScalingNode arrow;
    private ScalingNode endNode;

    private final Map<AugmentedImage, AugmentedNode> augmentedImageMap = new HashMap<>();

    private World world;
    private Destination destination;
    private PathFinder pathFinder;

    private SensorManager sensorManager;

    private PathView pathView;
    private Map<Integer, Bitmap> imageMap;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this))
            return;

        requestWindowFeature(R.attr.windowNoTitle);

        setContentView(R.layout.activity_ux);

        initMapAndDropdown();

        mapView = findViewById(R.id.mapView);
        View toggleMapButton = findViewById(R.id.toggleMapButton);
        toggleMapButton.setOnClickListener((c) -> mapView.animate().alpha(mapView.getAlpha() == 1.0 ? 0.0f : 1.0f));

        pathFinder = new PathFinder();

        pathView = findViewById(R.id.PathView);
        sensorManager = new SensorManager(this);

        arrow = new ScalingNode(this, "arrow.sfb", 2.5f);
        endNode = new ScalingNode(this, "flagpole.sfb", 0.3f);
        arFragment = (AugmentedImageFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
        Session session = null;
        try {
            session = new Session(this);
        } catch (UnavailableArcoreNotInstalledException e) {
            e.printStackTrace();
        } catch (UnavailableApkTooOldException e) {
            e.printStackTrace();
        } catch (UnavailableSdkTooOldException e) {
            e.printStackTrace();
        }

        configureSession(session);
    }

    private void configureSession(Session session) {
        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        session.configure(config);
        arFragment.getSessionConfiguration(session);
    }

    private void initMapAndDropdown() {
        MapReader mp = new MapReader();
        world = mp.readFile(this);
        imageMap = new DestinationBitmapReader(this).ReadBitmaps();
        Spinner dropdown = findViewById(R.id.spinner);
        ArrayList<ItemData> list = new ArrayList<>();
        list.add(new ItemData(getString(R.string.DestinationSpinnerDefault), null));
        list.addAll(world.getDestinations().stream().map((x) -> new ItemData(x, imageMap.get(x.getImageIndex()))).collect(Collectors.toList()));
        ImageSpinnerAdapter adapter = new ImageSpinnerAdapter(this, R.layout.spinner_layout, R.id.txt, list);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(onDropdownSelect());
    }

    private AdapterView.OnItemSelectedListener onDropdownSelect() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                //Position == 0 is default hinted message
                if (position != 0) {
                    destination = (Destination) ((ItemData) spinner.getItemAtPosition(position)).getDest();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();

        if (frame == null || frame.getCamera().getTrackingState() != TrackingState.TRACKING)
            return;

        Collection<AugmentedImage> updatedAugmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);

        for (AugmentedImage augmentedImage : updatedAugmentedImages) {
            switch (augmentedImage.getTrackingState()) {
                case PAUSED:
                    String text = "Detected Image " + augmentedImage.getIndex();
                    Toast.makeText(this, text, Toast.LENGTH_LONG).show();
                    break;
                case TRACKING:
                    if (!augmentedImageMap.containsKey(augmentedImage)) {
                        augmentedImageMap.put(augmentedImage, arrow);
                        Grid begin = world.getDestination(augmentedImage.getIndex());

                        if (destination != null && world != null) {
                            if (begin != null && destination != begin) {
                                arrow.setParent(null);
                                endNode.setParent(null);
                                //sensorManager.updateOrientationAngles();
                                //float angleBetweenDeviceAndNorth = sensorManager.getmOrientationAngles()[0];
                                //float dxZ = sensorManager.getAccelerometerReading()[2];
                                List<Grid> path = pathFinder.calculateShortestPath(world, world.getGrid(begin.getX(), begin.getY()), world.getGrid(destination.getX(), destination.getY()));
                                Destination closestDst = pathFinder.getClosestDestination(world, path);
                                List<Destination> dsts = pathFinder.getDestinationsFromPath(world, path);
                                pathView.setNavigationPoints(dsts, imageMap);
                                mapView.setPath(path, dsts, imageMap, world);
                                int xDir = closestDst.getX() - begin.getX(), yDir = closestDst.getY() - begin.getY();
                                double yAngle = xDir != 0 ? Math.toDegrees(Math.tan(yDir / xDir)) :
                                        ( yDir > 0 ?  Math.toDegrees((3*Math.PI) / 2) : Math.toDegrees(Math.PI / 2));
                                arrow.renderNode(augmentedImage, arFragment, (node) -> node.setWorldRotation(Quaternion.multiply(Quaternion.axisAngle(new Vector3(1.0f, 0.0f, 0.0f), 90f)
                                        , Quaternion.axisAngle(new Vector3(0f, 1f, 0f), (float) yAngle))));

                                TextView textView = findViewById(R.id.textView);
                                String distanceString = "~" + path.size() * Grid.GridResolution + "m";
                                textView.setText(distanceString);
                                textView.setBackgroundResource(R.color.colorPrimary);

                            } else {
                                arrow.setParent(null);
                                endNode.setParent(null);
                                endNode.renderNode(augmentedImage, arFragment, (node) -> node.setLocalRotation(
                                        Quaternion.multiply(Quaternion.axisAngle(new Vector3(1.0f, 0.0f, 0.0f), 90f)
                                                , Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), -90))));
                                TextView textView = findViewById(R.id.textView);
                                textView.setBackgroundResource(R.color.colorPrimary);
                                textView.setText("0m");
                            }
                        }
                    }
                    break;
                case STOPPED:
                    augmentedImageMap.remove(augmentedImage);
                    break;
            }
        }

    }

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGLVersionString = ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                .getDeviceConfigurationInfo()
                .getGlEsVersion();
        if (Double.parseDouble(openGLVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 or later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        return true;
    }
}
