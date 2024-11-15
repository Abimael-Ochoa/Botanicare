package com.example.projectintegration;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PlantAdapter extends BaseAdapter {

    private Context context;
    private List<Plant> plantList;

    public PlantAdapter(Context context, List<Plant> plantList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.plants_item, parent, false);
        }

        // Obtén la planta actual
        Plant plant = plantList.get(position);

        // Referencias a los elementos de la vista
        TextView nameTextView = convertView.findViewById(R.id.plantNameTextView);
        TextView quantityTextView = convertView.findViewById(R.id.plantQuantityTextView);
        ImageView imageView = convertView.findViewById(R.id.plantImageView);


        // Llenar los elementos con datos
        nameTextView.setText(plant.getName());
        quantityTextView.setText("Cantidad: " + plant.getQuantity());

        String imageUrl = plant.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty() || imageUrl.equals("")) {
            // Si la URL es null o vacía, usa una imagen predeterminada
            imageView.setImageResource(R.drawable.ic_plant);  // Asegúrate de tener un drawable con ese nombre
        } else {
            // Si la URL es válida, carga la imagen con Glide
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_plant)  // Imagen predeterminada mientras se carga
                    .error(R.drawable.ic_plant)  // Imagen que se muestra si hay un error al cargar la imagen
                    .into(imageView);
        }
        return convertView;
    }
}

