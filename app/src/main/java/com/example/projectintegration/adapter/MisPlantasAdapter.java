package com.example.projectintegration.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projectintegration.PlantInformationActivity;
import com.example.projectintegration.R;
import com.example.projectintegration.models.Plant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MisPlantasAdapter extends RecyclerView.Adapter<MisPlantasAdapter.PlantViewHolder> {

    private Context context;
    private List<Plant> plantList;

    public MisPlantasAdapter(Context context, List<Plant> plantList) {
        this.context = context;
        this.plantList = plantList;
    }

    // Se llama cuando se crea un nuevo ViewHolder
    @Override
    public PlantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_misplantas, parent, false);
        return new PlantViewHolder(itemView);
    }

    // Se llama para asignar los datos al ViewHolder
    @Override
    public void onBindViewHolder(PlantViewHolder holder, int position) {
        Plant plant = plantList.get(position);

        // Asigna los datos del plant al UI
        holder.plantName.setText(plant.getName());

        // Cargar la imagen de la planta usando Picasso o cualquier otra librería
        Picasso.get().load(plant.getImageUrl()).into(holder.plantImage);

        // Evento de clic para redirigir a la información de la planta
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlantInformationActivity.class);
            intent.putExtra("plantName", plant.getName());
            intent.putExtra("plantDescription", plant.getDescription());
            intent.putExtra("plantImage", plant.getImageUrl());
            intent.putExtra("plantQuantity", plant.getQuantity());
            context.startActivity(intent);
        });
    }

    // Devuelve el número de elementos en la lista
    @Override
    public int getItemCount() {
        return plantList.size();
    }

    // ViewHolder que contiene las vistas de cada item
    public static class PlantViewHolder extends RecyclerView.ViewHolder {
        public TextView plantName;
        public ImageView plantImage;

        public PlantViewHolder(View itemView) {
            super(itemView);
            plantName = itemView.findViewById(R.id.plantName);
            plantImage = itemView.findViewById(R.id.plantImage);
        }
    }
}
