package nl.carlodvm.androidapp.Core;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DestinationBitmapReader {
    private Activity activity;
    String[] fileNames = {
            "0_ENTR.jpg",
            "1_CAF.jpg",
            "2_STAIR.jpg",
            "3_POLI.jpg",
            "4_FIRST.jpg",
            "5_SCND.jpg",
            "6_THRD.jpg",
            "7_GARAG.jpg",
            "8_REST.jpg",
            "9_RECEPTIE.jpg"
    };

    public DestinationBitmapReader(Activity activity) {
        this.activity = activity;
    }

    public Map<Integer, Bitmap> ReadBitmaps() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        HashMap<Integer, Bitmap> bitmapHashMap = new HashMap<Integer, Bitmap>();
        for (String fileName : fileNames) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(activity.getAssets().open(fileName));
                bitmapHashMap.put(Integer.parseInt(fileName.split("_")[0]), bitmap);
            } catch (IOException e) {
                Log.e(DestinationBitmapReader.class.getSimpleName(), "Could not find bitmaps.");
            }
        }
        return bitmapHashMap;
    }
}
