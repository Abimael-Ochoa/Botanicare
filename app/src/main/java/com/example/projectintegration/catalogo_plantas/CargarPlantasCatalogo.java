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
    private List<Plant> plantList;     // Lista para el GridView (Catálogo)
    private FirebaseFirestore db;
    private final Handler searchHandler = new Handler();  // Handler para manejar el debounce
    private Runnable searchRunnable;

    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__catalogo_plantas, container, false);

        // Inicializa el RecyclerView para "Mis Plantas"
        plantsRecyclerView = view.findViewById(R.id.plantsRecyclerView);
        myPlantsList = new ArrayList<>();

        // Configura el RecyclerView para el desplazamiento horizontal
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        plantsRecyclerView.setLayoutManager(layoutManager);

        // Agrega el LinearSnapHelper para alinear los elementos
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(plantsRecyclerView);

        // Crea una instancia del adaptador para "Mis Plantas"
        misPlantasAdapter = new MisPlantasAdapter(getContext(), myPlantsList);
        plantsRecyclerView.setAdapter(misPlantasAdapter);

        // Inicializa el GridView para el Catálogo de Plantas
        plantsGridView = view.findViewById(R.id.plantsGridView);
        plantList = new ArrayList<>();
        plantAdapter = new PlantAdapter(getContext(), plantList);
        plantsGridView.setAdapter(plantAdapter);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Inicializa Firestore y carga los datos
        db = FirebaseFirestore.getInstance();
        loadPlantsFromFirebase();          // Carga el catálogo
        loadPlantsFromFirebaseMyPlants();  // Carga "Mis Plantas"

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

    // Método para filtrar plantas (Catálogo)
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

    // Ajustar columnas dinámicamente
    private void adjustGridViewColumns() {
        plantsGridView.post(() -> {
            int totalWidth = plantsGridView.getWidth();
            int columnWidth = getResources().getDimensionPixelSize(R.dimen.column_width);
            int numColumns = totalWidth / columnWidth;
            if (numColumns < 2) numColumns = 2;
            plantsGridView.setNumColumns(numColumns);
        });
    }

    // Carga catálogo de plantas
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

    private void loadPlantsFromFirebaseMyPlants() {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        myPlantsList.clear(); // Asegúrate de limpiar la lista antes de cargar
                        List<Map<String, Object>> plantItems = (List<Map<String, Object>>) documentSnapshot.get("plantItems");
                        if (plantItems != null) {
                            int totalPlants = plantItems.size();
                            int[] plantsLoaded = {0};

                            for (Map<String, Object> plantItem : plantItems) {
                                String plantName = (String) plantItem.get("plantName");
                                Long quantityLong = (Long) plantItem.get("quantity");
                                int quantity = (quantityLong != null) ? quantityLong.intValue() : 0;

                                db.collection("plants").whereEqualTo("name", plantName).get()
                                        .addOnSuccessListener(querySnapshot -> {
                                            Log.d("FirebaseQuery", "Consulta realizada para: " + plantName + " - Documentos encontrados: " + querySnapshot.size());
                                            if (!querySnapshot.isEmpty()) {
                                                DocumentSnapshot plantDoc = querySnapshot.getDocuments().get(0);
                                                Plant plant = plantDoc.toObject(Plant.class);
                                                if (plant != null) {
                                                    plant.setQuantity(quantity);
                                                    myPlantsList.add(plant);
                                                    Log.d("PlantAdded", "Planta añadida: " + plant.getName() + ", Cantidad: " + plant.getQuantity());
                                                }
                                            } else {
                                                Log.d("Firestore", "No se encontró la planta: " + plantName);
                                            }

                                            plantsLoaded[0]++;
                                            Log.d("PlantLoadStatus", "Cargadas: " + plantsLoaded[0] + "/" + totalPlants);

                                            // Notifica al adaptador solo cuando todas las plantas estén cargadas
                                            if (plantsLoaded[0] == totalPlants) {
                                                misPlantasAdapter.notifyDataSetChanged();
                                                Log.d("AdapterUpdated", "Adaptador actualizado con " + myPlantsList.size() + " elementos");
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("FirestoreError", "Error al buscar planta en colección 'plants'", e);
                                        });
                            }
                        } else {
                            Toast.makeText(getContext(), "No hay plantas en plantItems", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error al cargar las plantas del usuario", e);
                    Toast.makeText(getContext(), "Error al cargar plantas", Toast.LENGTH_SHORT).show();
                });
    }

}
