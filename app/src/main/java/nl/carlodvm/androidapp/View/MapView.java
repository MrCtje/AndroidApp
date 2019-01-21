package nl.carlodvm.androidapp.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import java.util.List;
import java.util.Map;

import nl.carlodvm.androidapp.Core.Destination;
import nl.carlodvm.androidapp.Core.Grid;
import nl.carlodvm.androidapp.Core.World;

public class MapView extends AppCompatImageView {
    List<Grid> grids;
    List<Destination> dests;
    Map<Integer, Bitmap> imageMap;
    World world;

    public MapView(Context context) {
        super(context);
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPath(List<Grid> grids, List<Destination> dests, Map<Integer, Bitmap> imageMap, World world) {
        this.grids = grids;
        this.dests = dests;
        this.imageMap = imageMap;
        this.world = world;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (grids != null && dests != null && world != null && imageMap != null) {
            int width = getWidth();
            int height = getHeight();
            int gWidth = width / world.getWidth();
            int gHeight = height / world.getHeight();
            dests.stream().forEach(x -> {
                int left = x.getX() * gWidth;
                int top = x.getY() * gHeight;
                canvas.drawBitmap(imageMap.get(x.getImageIndex()),
                        null,
                        new Rect(left - 15, top - 15, left + 15, top + 15),
                        null);
            });
        }
    }
}
