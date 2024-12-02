package com.example.projectintegration;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.projectintegration.adapter.MisPlantasAdapter;
import com.example.projectintegration.models.Plant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentMisPlantas extends Fragment {
    private GridView plantsGridView;
    private List<Plant> myPlantsList;
    private MisPlantasAdapter misPlantasAdapter;
    private FirebaseFirestore db;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_plantas, container, false);

        plantsGridView = view.findViewById(R.id.myplantsGridView);
        myPlantsList = new ArrayList<>();

        // Configura el adaptador para el GridView
        misPlantasAdapter = new MisPlantasAdapter(getContext(), myPlantsList);
        plantsGridView.setAdapter(misPlantasAdapter);

        // Maneja el clic en los elementos del GridView
        plantsGridView.setOnItemClickListener((AdapterView<?> parent, View itemView, int position, long id) -> {
            Plant selectedPlant = myPlantsList.get(position);
            Toast.makeText(getContext(), "Seleccionaste: " + selectedPlant.getName(), Toast.LENGTH_SHORT).show();
        });

        // Inicializa Firebase y carga los datos
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        loadPlantsFromFirebaseMyPlants();

        return view;
    }

    private void loadPlantsFromFirebaseMyPlants() {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        myPlantsList.clear();

                        // Utilizamos un HashMap para almacenar plantas únicas por plantName
                        Map<String, Plant> uniquePlantsMap = new HashMap<>();

                        List<Map<String, Object>> plantItems = (List<Map<String, Object>>) documentSnapshot.get("plantItems");
                        if (plantItems != null) {
                            for (Map<String, Object> plantItem : plantItems) {
                                String plantName = (String) plantItem.get("plantName");
                                Long quantityLong = (Long) plantItem.get("quantity");
                                int quantity = (quantityLong != null) ? quantityLong.intValue() : 0;

                                // Realizamos la consulta a Firestore solo si el plantName aún no está en el HashMap
                                if (!uniquePlantsMap.containsKey(plantName)) {
                                    db.collection("plants").whereEqualTo("name", plantName).get()
                                            .addOnSuccessListener(querySnapshot -> {
                                                if (!querySnapshot.isEmpty()) {
                                                    DocumentSnapshot plantDoc = querySnapshot.getDocuments().get(0);
                                                    Plant plant = plantDoc.toObject(Plant.class);
                                                    if (plant != null) {
                                                        plant.setQuantity(quantity);
                                                        uniquePlantsMap.put(plantName, plant); // Agregamos al HashMap
                                                    }
                                                }

                                                // Cuando finalicen todas las consultas, actualizamos la lista del adaptador
                                                updatePlantListFromMap(uniquePlantsMap);
                                            })
                                            .addOnFailureListener(e -> Log.e("FirestoreError", "Error al buscar planta", e));
                                }
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

    // Método para actualizar el adaptador desde el HashMap
    private void updatePlantListFromMap(Map<String, Plant> uniquePlantsMap) {
        myPlantsList.clear();
        myPlantsList.addAll(uniquePlantsMap.values()); // Convertimos los valores únicos del HashMap en una lista
        misPlantasAdapter.notifyDataSetChanged();
    }

}
