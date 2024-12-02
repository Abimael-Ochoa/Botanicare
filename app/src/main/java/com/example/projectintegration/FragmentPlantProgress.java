package com.example.projectintegration;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


        // Inicializa Firebase y carga los datos
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Configurar botón de retroceso
        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed(); // Volver atrás
            }
        });

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Configurar GridView y lista de datos
        GridView gridView = view.findViewById(R.id.plantsGridView);
        items = new ArrayList<>();
        adapter = new AdapterPlantProgress(getContext(), items);
        gridView.setAdapter(adapter);

        // Cargar datos desde Firestore
        loadPlantData();

        return view;
    }

    private void loadPlantData() {
        // Reemplaza "C5txZ5Vw4FOAjvQSOuUt..." por el ID real o ajusta la lógica para obtener el documento correcto
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
                                    // Aquí puedes establecer una imagen predeterminada o ajustar según sea necesario
                                    items.add(new IPlantProgress(R.drawable.ic_plant, plantName));
                                }
                            }
                            adapter.notifyDataSetChanged(); // Actualizar el GridView
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
