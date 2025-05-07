package com.example.projectintegration.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.projectintegration.GaleriaProgreso;
import com.example.projectintegration.R;
import com.example.projectintegration.models.IPlantProgress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdapterPlantProgress extends BaseAdapter {
    private Context context;
    private List<IPlantProgress> items;
    private RequestQueue requestQueue;
    private FirebaseFirestore firestore;
    private TextView apodo;

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
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_plant_list, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.plant_image);
            holder.textView = convertView.findViewById(R.id.plant_name);
            holder.apodo = convertView.findViewById(R.id.apodo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        IPlantProgress currentItem = items.get(position);
        String plantName = currentItem.getPlantName();
        String uniqueId = currentItem.getUniqueId();
        String imageUrl = currentItem.getImageUrl();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        holder.textView.setText(plantName);
        holder.apodo.setText(""); // Limpiar antes de consultar
        holder.apodo.setVisibility(View.GONE); // Ocultar por defecto

// Consultar apodo desde Firestore solo si hay uniqueId
        if (uniqueId != null && !uniqueId.trim().isEmpty()) {
            firestore.collection("users")
                    .document(userId)
                    .collection("userPlants")
                    .whereEqualTo("uniqueId", uniqueId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        String nickname = null;

                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            String nick = doc.getString("plantNickName");
                            if (nick != null && !nick.trim().isEmpty()) {
                                nickname = nick; // tomar el primero válido
                                break;
                            }
                        }

                        if (nickname != null) {
                            holder.apodo.setText("Apodo: " + nickname);
                            holder.apodo.setVisibility(View.VISIBLE);
                        } else {
                            holder.apodo.setText(""); // limpiar por si acaso
                            holder.apodo.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(e -> {
                        holder.apodo.setText("");
                        holder.apodo.setVisibility(View.GONE);
                    });
        }

        // Cargar imagen si hay
        if (imageUrl != null) {
            loadImageWithVolley(imageUrl, holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_plant);
        }

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GaleriaProgreso.class);
            intent.putExtra("uniqueId", uniqueId);
            intent.putExtra("plantName", plantName);
            context.startActivity(intent);
        });

        return convertView;
    }

    // ViewHolder pattern para evitar múltiples llamadas a findViewById
    static class ViewHolder {
        ImageView imageView;
        TextView textView;
        TextView apodo;
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