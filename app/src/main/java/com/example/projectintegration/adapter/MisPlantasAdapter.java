package com.example.projectintegration.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.projectintegration.PlantInformationActivity;
import com.example.projectintegration.PlantInformationAdmin;
import com.example.projectintegration.R;
import com.example.projectintegration.models.Plant;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

public class MisPlantasAdapter extends BaseAdapter {

    private final Context context;
    private final List<Plant> plantList;
    private final com.android.volley.RequestQueue requestQueue;

    // Constructor
    public MisPlantasAdapter(Context context, List<Plant> plantList) {
        this.context = context;
        this.plantList = plantList;
        this.requestQueue = Volley.newRequestQueue(context);
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
        ViewHolder viewHolder;

        if (convertView == null) {
            // Infla la vista para el item
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_misplantas, parent, false);

            // Crea un nuevo ViewHolder y guarda las referencias a las vistas
            viewHolder = new ViewHolder();
            viewHolder.nameTextView = convertView.findViewById(R.id.misPlantasNombre);
            viewHolder.imageView = convertView.findViewById(R.id.misPlantasIMG);
            convertView.setTag(viewHolder);
        } else {
            // Recupera el ViewHolder para optimizar el rendimiento
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Obtiene la planta actual
        Plant plant = plantList.get(position);

        // Configura los datos en las vistas
        viewHolder.nameTextView.setText(plant.getName());

        String imageUrl = plant.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            viewHolder.imageView.setImageResource(R.drawable.ic_plant); // Imagen predeterminada
        } else {
            loadImageWithVolley(imageUrl, viewHolder.imageView); // Cargar la imagen usando Volley
        }

        convertView.setOnClickListener(v -> {
            String plantName = plant.getName(); // Nombre de la planta seleccionada

            // Consultar Firestore para obtener el conteo
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            int count = 0;

                            for (DocumentSnapshot document : task.getResult()) {
                                List<Map<String, Object>> plantItems = (List<Map<String, Object>>) document.get("plantItems");
                                if (plantItems != null) {
                                    for (Map<String, Object> item : plantItems) {
                                        if (plantName.equals(item.get("plantName"))) {
                                            count++;
                                        }
                                    }
                                }
                            }

                            // Inicia la actividad con los datos de la planta y la cantidad
                            Intent intent = new Intent(context, PlantInformationAdmin.class);
                            intent.putExtra("plantName", plant.getName());
                            intent.putExtra("plantDescription", plant.getDescription());
                            intent.putExtra("plantImage", plant.getImageUrl());
                            intent.putExtra("plantQuantity", count); // Cantidad total de plantas
                            intent.putExtra("scientificName", plant.getScientificName());
                            intent.putExtra("care", plant.getCare());
                            context.startActivity(intent);
                        } else {
                            // Manejar errores en la consulta
                            Toast.makeText(context, "Error al obtener la cantidad de plantas", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        return convertView;
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


    // ViewHolder para optimizar la conversión de vistas
    static class ViewHolder {
        TextView nameTextView;
        ImageView imageView;
    }
}
