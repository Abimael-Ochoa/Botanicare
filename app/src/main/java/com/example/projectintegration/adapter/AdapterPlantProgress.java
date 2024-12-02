package com.example.projectintegration.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.projectintegration.R;
import com.example.projectintegration.models.IPlantProgress;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdapterPlantProgress extends BaseAdapter {
    private Context context;
    private List<IPlantProgress> items;
    private RequestQueue requestQueue;
    private FirebaseFirestore firestore;

    public AdapterPlantProgress(Context context, List<IPlantProgress> items) {
        this.context = context;
        this.items = items;
        this.requestQueue = Volley.newRequestQueue(context);
        this.firestore = FirebaseFirestore.getInstance();
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
        //textView.setText(currentItem.getText());

        // Obtener y cargar la imagen desde Firebase usando el nombre de la planta
       // loadPlantImage(currentItem.getText(), imageView);

        return convertView;
    }

    private void loadPlantImage(String plantName, ImageView imageView) {
        // Consultar Firestore para obtener el imageUrl
        firestore.collection("plants")
                .whereEqualTo("name", plantName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String imageUrl = document.getString("imageUrl");
                        if (imageUrl != null) {
                            loadImageWithVolley(imageUrl, imageView);
                        } else {
                            imageView.setImageResource(R.drawable.ic_plant); // Imagen por defecto
                        }
                    } else {
                        imageView.setImageResource(R.drawable.ic_plant); // Imagen por defecto si no se encuentra
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    imageView.setImageResource(R.drawable.ic_plant); // Imagen por defecto en caso de error
                });
    }

    private void loadImageWithVolley(String url, final ImageView imageView) {
        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }
                },
                0, 0, null, null,
                error -> imageView.setImageResource(R.drawable.ic_plant)); // Imagen por defecto en caso de error

        requestQueue.add(imageRequest);
    }
}
