package com.example.projectintegration.catalogo_plantas;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.projectintegration.R;
import com.example.projectintegration.models.Plant;
import com.example.projectintegration.adapter.PlantAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CargarPlantasCatalogo extends Fragment {
    private GridView plantsGridView;
    private static PlantAdapter plantAdapter;
    private static List<Plant> plantList;
    private FirebaseFirestore db;
    private final Handler searchHandler = new Handler();  // Handler para manejar el debounce
    private Runnable searchRunnable;               // Runnable para la búsqueda


    @Override
    public  View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__catalogo_plantas, container, false);

        // Inicializa el GridView y la lista de plantas
        plantsGridView = view.findViewById(R.id.plantsGridView);
        plantList = new ArrayList<>();
        plantAdapter = new PlantAdapter(getContext(), plantList);

        // Asocia el adaptador al GridView
        plantsGridView.setAdapter(plantAdapter);

        // Inicializa Firestore y carga las plantas
        db = FirebaseFirestore.getInstance();
        loadPlantsFromFirebase();

        return view;
    }


    public void filterPlants(String query) {
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        searchRunnable = () -> {
            if (query.isEmpty()) {
                db.collection("plants")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            plantList.clear();
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                Plant plant = document.toObject(Plant.class);
                                plantList.add(plant);
                            }
                            plantAdapter.notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), "Error al buscar plantas", Toast.LENGTH_SHORT).show();
                        });

            } else {
                db.collection("plants")
                        .whereGreaterThanOrEqualTo("name", query)
                        .whereLessThanOrEqualTo("name", query + "\uf8ff")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            plantList.clear();
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                Plant plant = document.toObject(Plant.class);
                                plantList.add(plant);
                            }
                            plantAdapter.notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), "Error al buscar plantas", Toast.LENGTH_SHORT).show();
                        });
            }
        };
        searchHandler.postDelayed(searchRunnable, 300);

    }

    private void adjustGridViewColumns() {
        plantsGridView.post(() -> {
            int totalWidth = plantsGridView.getWidth(); // Obtiene el ancho total disponible
            int columnWidth = getResources().getDimensionPixelSize(R.dimen.column_width); // Define un ancho base
            int numColumns = totalWidth / columnWidth;

            if (numColumns < 2) numColumns = 2; // Asegura al menos 2 columnas
            plantsGridView.setNumColumns(numColumns);
        });
    }

        // Cargar plantas desde Firebase
        private void loadPlantsFromFirebase () {
            db.collection("plants")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        plantList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            // Convierte el documento de Firestore a un objeto Plant
                            Plant plant = document.toObject(Plant.class);
                            plantList.add(plant);
                        }
                        // Notificar al adaptador que los datos han cambiado
                        plantAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al cargar plantas", Toast.LENGTH_SHORT).show();
                    });
        }
    }

