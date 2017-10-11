package com.example.davegilbier.listdream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Dave Gilbier on 09/10/2017.
 */

public class DreamListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Dream> dreamList;

    public DreamListAdapter(Context context, int layout, ArrayList<Dream> dreamList) {
        this.context = context;
        this.layout = layout;
        this.dreamList = dreamList;
    }

    @Override
    public int getCount() {
        return dreamList.size();
    }

    @Override
    public Object getItem(int position) {
        return dreamList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtDream, txtDescription;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtDream = (TextView) row.findViewById(R.id.txtDream);
            holder.txtDescription = (TextView) row.findViewById(R.id.txtDescription);
            holder.imageView = (ImageView) row.findViewById(R.id.imgDream);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Dream dream = dreamList.get(position);

        holder.txtDream.setText(dream.getDream());
        holder.txtDescription.setText(dream.getDescription());

        byte[] dreamImage = dream.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(dreamImage, 0, dreamImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }
}
