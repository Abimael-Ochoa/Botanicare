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
import com.example.projectintegration.R;
import com.example.projectintegration.models.Plant;

import java.util.List;

public class PlantAdapter extends BaseAdapter {

    private Context context;
    private List<Plant> plantList;
    private com.android.volley.RequestQueue requestQueue;

    public PlantAdapter(Context context, List<Plant> plantList) {
        this.context = context;
        this.plantList = plantList;
        this.requestQueue = Volley.newRequestQueue(context); // Inicializa la cola de solicitudes
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

        // Define el tamaño deseado en píxeles (ajusta según tus necesidades)
        int itemWidth = 450;  // Ancho del elemento
        int itemHeight = 400; // Alto del elemento

        // Ajusta el tamaño de la vista del elemento
        convertView.getLayoutParams().width = itemWidth;
        convertView.getLayoutParams().height = itemHeight;

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
        if (imageUrl == null || imageUrl.isEmpty()) {
            // Si la URL es null o vacía, usa una imagen predeterminada
            imageView.setImageResource(R.drawable.ic_plant);
        } else {
            // Si la URL es válida, carga la imagen con Volley
            loadImageWithVolley(imageUrl, imageView);
        }

        return convertView;
    }

    // Método para cargar la imagen con Volley
    private void loadImageWithVolley(String url, final ImageView imageView) {
        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        // Si la imagen se carga correctamente, la colocamos en el ImageView
                        imageView.setImageBitmap(response);
                    }
                },
                0, 0, null, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        // Si hay un error al cargar la imagen, mostramos una imagen predeterminada
                        imageView.setImageResource(R.drawable.ic_plant);
                    }
                });

        // Agrega la solicitud a la cola de solicitudes de Volley
        requestQueue.add(imageRequest);
    }
}
