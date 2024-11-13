package com.example.projectintegration;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.List;

public class PlantAdapter {

    private Context context;
    private List<Plant> plantList;

    public PlantAdapter(Context context, List<Plant> plantList) {
        this.context = context;
        this.plantList = plantList;
    }

    // Método para llenar el GridLayout con las plantas
    public void loadPlantsToGrid(GridLayout gridLayout) {
        gridLayout.removeAllViews();  // Limpiar el GridLayout

        for (final Plant plant : plantList) {
            View plantView = LayoutInflater.from(context).inflate(R.layout.plant_item, gridLayout, false);

            // Encontrar las vistas dentro del item
            ImageView plantImage = plantView.findViewById(R.id.plantImage);
            TextView plantName = plantView.findViewById(R.id.plantName);
            TextView plantQuantity = plantView.findViewById(R.id.plantQuantity);

            // Usar Glide para cargar la imagen de la planta
            Glide.with(context).load(plant.getImageUrl()).into(plantImage);

            // Establecer los valores de los datos
            plantName.setText(plant.getName());
            plantQuantity.setText("Cantidad: " + plant.getQuantity());

            // Agregar la vista al GridLayout
            gridLayout.addView(plantView);
        }
    }
}
