package nl.carlodvm.androidapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.google.ar.core.*;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.FrameTime;
import nl.carlodvm.androidapp.Animation.ScalingNode;
import nl.carlodvm.androidapp.Core.LocationManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private AugmentedImageFragment arFragment;
    private LocationManager locationManager;

    private ScalingNode arrow;

    private final Map<AugmentedImage, AugmentedNode> augmentedImageMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this))
            return;

        setContentView(R.layout.activity_ux);

        locationManager = new LocationManager(this, this);

        arrow = new ScalingNode(this, "arrow.sfb");
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
        // IMPORTANT!!!  ArSceneView requires the `LATEST_CAMERA_IMAGE` non-blocking update mode.
        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        session.configure(config);
        arFragment.getSessionConfiguration(session);
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

                        arrow.renderNode(augmentedImage, arFragment);

                        String loc = locationManager.GetModelGPSLocation(arrow).toString();
                        Toast.makeText(this, loc, Toast.LENGTH_LONG).show();
                        Log.e(TAG, loc);
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
