package com.example.projectintegration;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectintegration.adapter.GaleriaProgresoAdapter;
import com.example.projectintegration.models.PlantsMonitoring;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class GaleriaProgreso extends AppCompatActivity {

    private TextView tvHeaderTitle;
    private LinearLayout btnSubir;
    private boolean isAdmin;
    private String uniqueId;
    private String plantName;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria_progreso);
        uniqueId = getIntent().getStringExtra("uniqueId");
        plantName = getIntent().getStringExtra("plantName");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = firebaseUser.getEmail(); // Obtener el correo electrónico del usuario

        if ("admin@admin.com".equalsIgnoreCase(email.trim())) {
            isAdmin = true;
        } else {
            isAdmin = false;
        }

        // Initialize views
        tvHeaderTitle = findViewById(R.id.header);  // Assuming you have a TextView with this ID in your layout
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(firebaseUser.getUid())
                .collection("userPlants")
                .whereEqualTo("uniqueId", uniqueId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String header;

                    if (!queryDocumentSnapshots.isEmpty()) {
                        String nickName = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1)
                                .getString("plantNickName");

                        header = (nickName != null && !nickName.trim().isEmpty())
                                ? "Progreso de " + nickName
                                : "Progreso de " + plantName;
                    } else {
                        header = "Progreso de " + plantName;
                    }

                    tvHeaderTitle.setText(header);
                })
                .addOnFailureListener(e -> {
                    tvHeaderTitle.setText("Progreso de " + plantName);
                });

        btnSubir = findViewById(R.id.subir);  // Assuming you have a LinearLayout with this ID in your layout

        // Get data from Intent
        String userName = getIntent().getStringExtra("userName");

        // Set up the back button
        ImageView btnBack = findViewById(R.id.btn_back);

        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Suponiendo que tienes un objeto 'plant' con el uniqueId y el nombre de la planta
                // Crear Intent para pasar a MonitoreoPlantas
                try {
                    Intent intent = new Intent(GaleriaProgreso.this, MonitoreoPlantas.class);
                    intent.putExtra("uniqueId", uniqueId); // Pasar el uniqueId
                    intent.putExtra("plantName", plantName); // Pasar el nombre de la planta
                    startActivity(intent);
                }catch (Exception e){
                    System.out.println("Error en:" + e.getMessage());
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // Update the header text based on user
        if (isAdmin) {
            // If the user is admin, hide the "Subir progreso" button
            btnSubir.setVisibility(View.GONE);
            // Set the header text to show the admin name
            tvHeaderTitle.setText("Progreso de " + userName);
        } else {
            // If the user is not an admin, display the normal gallery title with the user's name
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlantProgressData(); // 🔄 Se llama cada vez que la Activity vuelve al frente
    }

    private void loadPlantProgressData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(firebaseUser.getUid())
                .collection("userPlants")
                .whereEqualTo("uniqueId", uniqueId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<PlantsMonitoring> plantProgressList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String imageUrl = document.getString("imageUrl");
                            String notes = document.getString("notes");
                            // Crear un objeto PlantsMonitoring y agregarlo a la lista
                            plantProgressList.add(new PlantsMonitoring(plantName, imageUrl, notes, null, uniqueId));
                        }

                        // Si se encontró el progreso, mostramos la imagen y nota
                        if (!plantProgressList.isEmpty()) {
                            GaleriaProgresoAdapter adapter = new GaleriaProgresoAdapter(GaleriaProgreso.this, plantProgressList);
                            GridView gridView = findViewById(R.id.plantsGridView);
                            gridView.setAdapter(adapter);
                        } else {
                            Toast.makeText(GaleriaProgreso.this, "No se encontraron registros de progreso para esta planta.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(GaleriaProgreso.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
