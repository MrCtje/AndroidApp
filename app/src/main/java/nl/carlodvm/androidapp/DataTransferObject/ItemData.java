package nl.carlodvm.androidapp.DataTransferObject;

import android.graphics.Bitmap;

public class ItemData {
    Object dest;
    Bitmap image;

    public ItemData(Object dest, Bitmap image) {
        this.dest = dest;
        this.image = image;
    }

    public Object getDest() {
        return dest;
    }

    public Bitmap getImage() {
        return image;
    }
}
