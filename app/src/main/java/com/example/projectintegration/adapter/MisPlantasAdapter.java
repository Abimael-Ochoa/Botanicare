package com.example.projectintegration.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.projectintegration.PlantInformationActivity;
import com.example.projectintegration.R;
import com.example.projectintegration.models.Plant;

import android.content.Intent;

import java.util.List;

public class MisPlantasAdapter extends RecyclerView.Adapter<MisPlantasAdapter.PlantViewHolder> {

    private final Context context;
    private final List<Plant> plantList;
    private final com.android.volley.RequestQueue requestQueue;

    // Constructor
    public MisPlantasAdapter(Context context, List<Plant> plantList) {
        this.context = context;
        this.plantList = plantList;
        this.requestQueue = Volley.newRequestQueue(context);
        // Verifica el tamaño de la lista para asegurarse de que no está vacía
        Log.d("MisPlantasAdapter", "Size of plantList: " + plantList.size());
    }

    // ViewHolder para optimizar el RecyclerView
    public static class PlantViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView imageView;

        public PlantViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.misPlantasNombre);
            imageView = itemView.findViewById(R.id.misPlantasIMG);
        }
    }

    @Override
    public PlantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflar el layout del item
        Log.d("MisPlantasAdapter", "onCreateViewHolder called for position: " + viewType);
        View view = LayoutInflater.from(context).inflate(R.layout.item_misplantas, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlantViewHolder holder, int position) {
        // Verifica que el RecyclerView esté recibiendo las posiciones correctamente
        Log.d("MisPlantasAdapter", "onBindViewHolder called for position: " + position);

        // Obtener la planta actual
        Plant plant = plantList.get(position);

        // Configurar los datos en las vistas
        holder.nameTextView.setText(plant.getName());

        String imageUrl = plant.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.imageView.setImageResource(R.drawable.ic_plant); // Imagen predeterminada
        } else {
            loadImageWithVolley(imageUrl, holder.imageView); // Cargar la imagen usando Volley
        }

        // Evento de clic para abrir PlantInformationActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlantInformationActivity.class);
            intent.putExtra("plantName", plant.getName());
            intent.putExtra("plantDescription", plant.getDescription());
            intent.putExtra("plantImage", plant.getImageUrl());
            intent.putExtra("plantQuantity", plant.getQuantity());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    // Método para cargar la imagen con Volley
    private void loadImageWithVolley(String url, final ImageView imageView) {
        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }
                },
                0, 0, null, null,
                error -> imageView.setImageResource(R.drawable.ic_plant));

        requestQueue.add(imageRequest);
    }

    // Método para actualizar la lista de plantas y notificar al adaptador
    public void updatePlantList(List<Plant> newPlantList) {
        this.plantList.clear();
        this.plantList.addAll(newPlantList);
        notifyDataSetChanged(); // Notificar que los datos han cambiado
        Log.d("MisPlantasAdapter", "Updated plantList size: " + plantList.size());
    }
}
