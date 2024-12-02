package com.example.projectintegration.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.projectintegration.R;
import com.example.projectintegration.models.IPlantProgress;

import java.util.List;

public class AdapterPlantProgress extends BaseAdapter {
    private Context context;
    private List<IPlantProgress> items;

    public AdapterPlantProgress(Context context, List<IPlantProgress> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_plant_list, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.plant_image);
        TextView textView = convertView.findViewById(R.id.plant_name);

        IPlantProgress currentItem = items.get(position);
        imageView.setImageResource(currentItem.getImageResId());

        return convertView;
    }
}

