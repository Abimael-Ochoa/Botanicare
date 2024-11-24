package com.example.projectintegration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PlantsGridFragment extends Fragment {
    private GridView plantsGridView;
    private static PlantAdapter plantAdapter;
    private static List<Plant> plantList;
    private FirebaseFirestore db;

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
        String searchQuery = query;
        db.collection("plants")
                .whereGreaterThanOrEqualTo("name", searchQuery)
                .whereLessThanOrEqualTo("name", searchQuery + "\uf8ff")
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


    // Cargar plantas desde Firebase
    private void loadPlantsFromFirebase() {
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

