package com.example.projectintegration.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.projectintegration.R;
import com.example.projectintegration.models.PlantsMonitoring;

import java.util.List;

public class GaleriaProgresoAdapter extends BaseAdapter {

    private Context context;
    private List<PlantsMonitoring> plantsList;

    public GaleriaProgresoAdapter(Context context, List<PlantsMonitoring> plantsList) {
        this.context = context;
        this.plantsList = plantsList;
    }

    @Override
    public int getCount() {
        return plantsList.size();
    }

    @Override
    public Object getItem(int position) {
        return plantsList.get(position);
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

        // Obtener la planta de la lista
        PlantsMonitoring plant = plantsList.get(position);

        // Inicializar las vistas
        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView tvDescription = convertView.findViewById(R.id.tv_description);

        // Mostrar imagen y descripción
        // Puedes usar una librería como Glide o Picasso para cargar la imagen desde una URL
        Glide.with(context)
                .load(plant.getImageUrl()) // Asumiendo que tienes la URL de la imagen
                .into(imageView);

        tvDescription.setText(plant.getNotes()); // Mostrar la nota

        return convertView;
    }
}
