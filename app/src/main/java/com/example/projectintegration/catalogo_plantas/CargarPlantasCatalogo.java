package com.example.projectintegration.catalogo_plantas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectintegration.PlantInformationActivity;
import com.example.projectintegration.R;
import com.example.projectintegration.adapter.MisPlantasAdapter;
import com.example.projectintegration.models.Plant;
import com.example.projectintegration.adapter.PlantAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CargarPlantasCatalogo extends Fragment {
    private RecyclerView plantsRecyclerView;
    private GridView plantsGridView;
    private static MisPlantasAdapter misPlantasAdapter;
    private static PlantAdapter plantAdapter;
    private List<Plant> myPlantsList;  // Lista para el RecyclerView (Mis Plantas)
    private List<Plant> plantList;     // Lista para el GridView (CatÃ¡logo)
    private FirebaseFirestore db;
    private final Handler searchHandler = new Handler();  // Handler para manejar el debounce
    private Runnable searchRunnable;

    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__catalogo_plantas, container, false);

        // Inicializa el GridView para el CatÃ¡logo de Plantas
        plantsGridView = view.findViewById(R.id.plantsGridView);
        plantList = new ArrayList<>();
        plantAdapter = new PlantAdapter(getContext(), plantList);
        plantsGridView.setAdapter(plantAdapter);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Inicializa Firestore y carga los datos
        db = FirebaseFirestore.getInstance();
        loadPlantsFromFirebase();          // Carga el catÃ¡logo

        // Configurar clic en GridView
        plantsGridView.setOnItemClickListener((AdapterView<?> parent, View view1, int position, long id) -> {
            if (!plantList.isEmpty()) {
                Plant selectedPlant = plantList.get(position);
                Log.d("GridViewClick", "Clicked on: " + selectedPlant.getName());

                Intent intent = new Intent(getContext(), PlantInformationActivity.class);
                intent.putExtra("plantName", selectedPlant.getName());
                intent.putExtra("plantDescription", selectedPlant.getDescription());
                intent.putExtra("plantImage", selectedPlant.getImageUrl());
                intent.putExtra("plantQuantity", selectedPlant.getQuantity());
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "No hay plantas disponibles", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // MÃ©todo para filtrar plantas (CatÃ¡logo)
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

    // Ajustar columnas dinÃ¡micamente
    private void adjustGridViewColumns() {
        plantsGridView.post(() -> {
            // Ancho total del GridView
            int totalWidth = plantsGridView.getWidth();

            // Ancho de cada columna (definido en dimensiones)
            int columnWidth = getResources().getDimensionPixelSize(R.dimen.column_width);

            // Espaciado horizontal entre columnas (definido en dimensiones)
            int horizontalSpacing = getResources().getDimensionPixelSize(R.dimen.horizontal_spacing);

            // Calcular el número máximo de columnas que caben
            int numColumns = totalWidth / (columnWidth + horizontalSpacing);

            // Asegurarse de que haya al menos 2 columnas
            if (numColumns < 2) numColumns = 2;

            // Ajustar el número de columnas dinámicamente
            plantsGridView.setNumColumns(numColumns);
        });
    }


    // Carga catÃ¡logo de plantas
    private void loadPlantsFromFirebase() {
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
                    Toast.makeText(getContext(), "Error al cargar plantas", Toast.LENGTH_SHORT).show();
                });
    }



}
