package com.example.projectintegration.catalogo_plantas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.projectintegration.PlantInformationActivity;
import com.example.projectintegration.R;
import com.example.projectintegration.adapter.PlantAdapter;
import com.example.projectintegration.models.Plant;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CargarPlantasCatalogo extends Fragment {
    private GridView plantsGridView;
    private PlantAdapter plantAdapter;
    private List<Plant> plantList;     // Lista filtrada
    private List<Plant> allPlantsList; // Lista completa
    private FirebaseFirestore db;
    private final Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__catalogo_plantas, container, false);

        plantsGridView   = view.findViewById(R.id.plantsGridView);
        plantList        = new ArrayList<>();
        allPlantsList    = new ArrayList<>();
        plantAdapter     = new PlantAdapter(getContext(), plantList);
        plantsGridView.setAdapter(plantAdapter);

        db = FirebaseFirestore.getInstance();
        loadPlantsFromFirebase();

        plantsGridView.setOnItemClickListener((AdapterView<?> parent, View v, int pos, long id) -> {
            Plant sel = plantList.get(pos);
            Intent i = new Intent(getContext(), PlantInformationActivity.class);
            i.putExtra("plantName",        sel.getName());
            i.putExtra("plantDescription", sel.getDescription());
            i.putExtra("plantImage",       sel.getImageUrl());
            i.putExtra("plantQuantity",    sel.getQuantity());
            startActivity(i);
        });

        return view;
    }

    /** Carga TODO el catálogo y lo deja en allPlantsList */
    private void loadPlantsFromFirebase() {
        db.collection("plants")
                .get()
                .addOnSuccessListener(snaps -> {
                    allPlantsList.clear();
                    for (DocumentSnapshot doc : snaps) {
                        Plant p = doc.toObject(Plant.class);
                        allPlantsList.add(p);
                    }
                    // Mostrar todo al inicio
                    plantList.clear();
                    plantList.addAll(allPlantsList);
                    plantAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al cargar plantas", Toast.LENGTH_SHORT).show()
                );
    }

    /**
     * Filtra case-insensitive (no consulta a Firestore, filtra en memoria).
     * Se llama desde tu SearchBarCatalogo con onSearchListener.onSearch(query).
     */
    public void filterPlants(String query) {
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        final String lowerQ = query.toLowerCase().trim();

        searchRunnable = () -> {
            plantList.clear();
            if (lowerQ.isEmpty()) {
                // Restaurar TODO
                plantList.addAll(allPlantsList);
            } else {
                // Filtrar por nombre ignorando mayúsculas
                for (Plant p : allPlantsList) {
                    if (p.getName() != null &&
                            p.getName().toLowerCase().contains(lowerQ)) {
                        plantList.add(p);
                    }
                }
            }
            plantAdapter.notifyDataSetChanged();
        };
        // Debounce 300ms
        searchHandler.postDelayed(searchRunnable, 300);
    }
}
