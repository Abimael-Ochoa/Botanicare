package com.example.projectintegration;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.example.projectintegration.adapter.AdapterPlantProgress;
import com.example.projectintegration.models.IPlantProgress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class FragmentPlantProgress extends Fragment {

    private FirebaseFirestore db;
    private AdapterPlantProgress adapter;
    private List<IPlantProgress> items;

    private String userId;

    public FragmentPlantProgress() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plant_progress, container, false);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Configurar botón de retroceso
        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Configurar GridView y lista de datos
        GridView gridView = view.findViewById(R.id.plantsGridView);
        items = new ArrayList<>();
        adapter = new AdapterPlantProgress(getContext(), items);
        gridView.setAdapter(adapter);

        // Configurar el Listener para la selección
        // Dentro de onCreateView de FragmentPlantProgress
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IPlantProgress selectedPlant = items.get(position);
                String plantName = selectedPlant.getPlantName();
                String uniqueId = selectedPlant.getUniqueId(); // Este es el uniqueId que necesitas

                // Crear el Intent para GaleriaProgreso
                Intent intent = new Intent(getContext(), GaleriaProgreso.class);
                intent.putExtra("plantName", plantName); // Pasar el nombre de la planta
                intent.putExtra("uniqueId", uniqueId);  // Pasar el uniqueId
                startActivity(intent); // Iniciar la actividad
            }
        });



        // Cargar datos desde Firestore
        loadPlantData();

        return view;
    }

    private void loadPlantData() {
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            List<?> plantItems = (List<?>) document.get("plantItems");
                            for (Object plantObj : plantItems) {
                                if (plantObj instanceof java.util.Map) {
                                    java.util.Map<String, Object> plantData = (java.util.Map<String, Object>) plantObj;
                                    String plantName = (String) plantData.get("plantName");
                                    String uniqueId = (String) plantData.get("uniqueId");

                                    // Consultar Firestore para obtener la imagen correspondiente
                                    db.collection("plants")
                                            .whereEqualTo("name", plantName)
                                            .get()
                                            .addOnSuccessListener(querySnapshot -> {
                                                if (!querySnapshot.isEmpty()) {
                                                    DocumentSnapshot plantDoc = querySnapshot.getDocuments().get(0);
                                                    String imageUrl = plantDoc.getString("imageUrl");

                                                    // Agregar datos al adaptador
                                                    items.add(new IPlantProgress(plantName, imageUrl, uniqueId)); // Pasa el uniqueId
                                                    adapter.notifyDataSetChanged();
                                                } else {
                                                    items.add(new IPlantProgress(plantName, null, uniqueId)); // Agregar sin imagen
                                                    adapter.notifyDataSetChanged();
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getContext(), "Error al cargar imagen de planta", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "No se encontraron plantas para este usuario", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
