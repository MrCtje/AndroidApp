package nl.carlodvm.androidapp.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.carlodvm.androidapp.Core.Destination;

public class PathView extends HorizontalScrollView {
    private LinkedList<Destination> destinationPath;
    private Destination currentNavigationPoint;
    private Map<Integer, Bitmap> imageMap;

    public PathView(Context context) {
        super(context);
    }

    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PathView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void addNavigationPoint(Destination destination) {
        destinationPath.add(destination);
    }

    public void setNavigationPoints(List<Destination> destination, Map<Integer, Bitmap> imageMap) {
        destinationPath = null;
        if(destination != null && imageMap != null) {
            destinationPath = new LinkedList<>(destination);
            if (destination.size() > 0)
                currentNavigationPoint = destination.get(0);

            setImageMap(imageMap);
        }
            invalidate();
            requestLayout();
    }

    public void selectNextPoint() {
        if (destinationPath.peekLast() != currentNavigationPoint) {
            currentNavigationPoint = destinationPath.get(destinationPath.indexOf(currentNavigationPoint) + 1);
            invalidate();
        }
    }

    public void setImageMap(Map<Integer, Bitmap> imageMap) {
        this.imageMap = imageMap;
        int width = getLayoutParams().width;
        int height = getLayoutParams().height;
        int ratio = width / height;
        imageMap.forEach((i, bitmap) -> bitmap = Bitmap.createScaledBitmap(bitmap, height, height, true));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (destinationPath != null && imageMap != null) {
            int ratio = getLayoutParams().width / getLayoutParams().height;

            int destCount = destinationPath.size();
            int spaceCount = ratio - destCount;
            boolean hasSpace = spaceCount > 1;

            // int imageWidth = width / ratio;
            int count = 0;
            for (Destination destination : destinationPath) {
                Bitmap bitmap = imageMap.get(destination.getImageIndex());
                int space = hasSpace ? (getLayoutParams().width / ratio * spaceCount / (spaceCount)) / 2 : 1;
                int imageWidth = getLayoutParams().height;
                canvas.drawBitmap(bitmap,
                        null,
                        new Rect((space + imageWidth) * count, 0, (space + imageWidth) * count + imageWidth, imageWidth)
                        , null);
                count++;
            }
        }
    }
}
