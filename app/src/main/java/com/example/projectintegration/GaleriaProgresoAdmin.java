package com.example.projectintegration;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectintegration.adapter.PlantAdapterProgress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GaleriaProgresoAdmin extends AppCompatActivity {

    private TextView tvHeaderTitle;
    private GridView plantsGridView;
    private FirebaseFirestore db;
    private LinearLayout subirProgreso;
    private String email;

    private PlantAdapterProgress plantAdapter;
    private List<Map<String, Object>> plantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria_progreso);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Obtener datos del Intent
        email = getIntent().getStringExtra("email");
        String userName = getIntent().getStringExtra("userName");

        // Inicializar vistas
        tvHeaderTitle = findViewById(R.id.header);
        plantsGridView = findViewById(R.id.plantsGridView);
        subirProgreso = findViewById(R.id.subirButon);

        subirProgreso.setVisibility(View.GONE);
        tvHeaderTitle.setText("Progreso de " + userName);

        // Inicializar la lista y adaptador
        plantList = new ArrayList<>();
        plantAdapter = new PlantAdapterProgress(this, plantList);
        plantsGridView.setAdapter(plantAdapter);

        // Botón para regresar
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> onBackPressed());

        // Cargar datos de Firestore
        loadUserPlantProgress();

        // Evento de clic en las imágenes
        plantsGridView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Map<String, Object> plantData = plantList.get(position);
            String notes = (String) plantData.get("notes");
            Toast.makeText(GaleriaProgresoAdmin.this, "Notas: " + notes, Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserPlantProgress() {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            db.collection("users")
                                    .document(document.getId())
                                    .collection("userPlants")
                                    .get()
                                    .addOnSuccessListener(snapshot -> {
                                        for (QueryDocumentSnapshot plantDocument : snapshot) {
                                            Map<String, Object> plantData = plantDocument.getData();
                                            plantList.add(plantData);
                                        }
                                        // Notificar al adaptador después de actualizar la lista
                                        plantAdapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> Log.e("Firestore Error", e.getMessage()));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore Error", e.getMessage()));
    }
}
