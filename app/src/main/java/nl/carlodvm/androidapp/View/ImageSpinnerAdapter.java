package nl.carlodvm.androidapp.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import nl.carlodvm.androidapp.DataTransferObject.ItemData;
import nl.carlodvm.androidapp.R;

public class ImageSpinnerAdapter extends ArrayAdapter<ItemData> {
    private int resource;
    private ArrayList<ItemData> list;
    private LayoutInflater inflater;
    private float density;

    public ImageSpinnerAdapter(Context context, int resource, int textViewResourceId, ArrayList<ItemData> objects) {
        super(context, textViewResourceId, objects);
        this.list = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        density = context.getResources().getDisplayMetrics().density;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = inflater.inflate(resource, parent, false);
        if (list.get(position).getImage() != null) {
            ImageView imageView = itemView.findViewById(R.id.image);
            imageView.setImageBitmap(list.get(position).getImage());
        }
        TextView textView = itemView.findViewById(R.id.txt);
        textView.setText(list.get(position).getDest().toString());
        return itemView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View itemView = getView(position, convertView, parent);
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        params.height = (int) (60 * density + 0.5f);
        itemView.setLayoutParams(params);
        return itemView;
    }
}
