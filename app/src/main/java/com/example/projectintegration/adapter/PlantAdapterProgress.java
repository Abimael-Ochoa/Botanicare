package com.example.projectintegration.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.BaseAdapter;

import com.example.projectintegration.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class PlantAdapterProgress extends BaseAdapter {

    private Context context;
    private List<Map<String, Object>> plantList;

    public PlantAdapterProgress(Context context, List<Map<String, Object>> plantList) {
        this.context = context;
        this.plantList = plantList;
    }

    @Override
    public int getCount() {
        return plantList.size();
    }

    @Override
    public Object getItem(int position) {
        return plantList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_galeria, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView tvDescription = convertView.findViewById(R.id.tv_description);

        Map<String, Object> plantData = plantList.get(position);

        // Cargar la imagen desde URL
        Picasso.get().load((String) plantData.get("imageUrl")).into(imageView);

        // Mostrar notas en el TextView
        tvDescription.setText((String) plantData.get("notes"));

        return convertView;
    }
}
