package nl.carlodvm.androidapp.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import nl.carlodvm.androidapp.Core.Destination;
import nl.carlodvm.androidapp.Core.Grid;
import nl.carlodvm.androidapp.Core.World;

public class MapView extends AppCompatImageView {
    private final int IMAGE_SIZE = 30;
    List<Grid> grids;
    List<Destination> dests;
    Map<Integer, Bitmap> imageMap;
    World world;
    Paint paint;

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

        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(4);
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (grids != null && dests != null && world != null && imageMap != null) {
            int width = getWidth() + (int) getX();
            int height = getHeight();
            int gWidth = width / world.getWidth();
            int gHeight = height / world.getHeight();

            for (ListIterator<Grid> it = grids.listIterator(); it.hasNext(); ) {
                Grid grid = it.next();
                Grid nextGrid = it.hasNext() ? grids.get(it.nextIndex()) : null;

                int x = (grid.getX() + 2) * gWidth + IMAGE_SIZE / 2;
                int y = height - (grid.getY() + 2) * gHeight;
                if (nextGrid != null) {
                    int nextX = (nextGrid.getX() + 2) * gWidth + IMAGE_SIZE / 2;
                    int nextY = height - (nextGrid.getY() + 2) * gHeight;
                    canvas.drawLine(x, y, nextX, nextY, paint);
                }
            }

            dests.stream().forEach(x -> {
                int left = (x.getX() + 2) * gWidth + IMAGE_SIZE / 2;
                int top = height - (x.getY() + 2) * gHeight;
                canvas.drawBitmap(imageMap.get(x.getImageIndex()),
                        null,
                        new Rect(left - IMAGE_SIZE, top - IMAGE_SIZE, left + IMAGE_SIZE, top + IMAGE_SIZE),
                        null);
            });
        }
    }
}
