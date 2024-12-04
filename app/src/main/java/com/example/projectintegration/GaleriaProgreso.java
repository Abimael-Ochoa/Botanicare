package com.example.projectintegration;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GaleriaProgreso extends AppCompatActivity {

    private TextView tvHeaderTitle;
    private LinearLayout btnSubir;
    private boolean isAdmin;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria_progreso);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = firebaseUser.getEmail(); // Obtener el correo electrónico del usuario

        if ("admin@admin.com".equalsIgnoreCase(email.trim())) {
            isAdmin = true;
        } else {
            isAdmin = false;
        }


        // Initialize views
        tvHeaderTitle = findViewById(R.id.header);  // Assuming you have a TextView with this ID in your layout
        btnSubir = findViewById(R.id.subir);  // Assuming you have a LinearLayout with this ID in your layout

        // Get data from Intent
        String userName = getIntent().getStringExtra("userName");

        // Set up the back button
        ImageView btnBack = findViewById(R.id.btn_back);

        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear Intent para abrir MonitoreoPlantas
                Intent intent = new Intent(GaleriaProgreso.this, MonitoreoPlantas.class);
                startActivity(intent);
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

}
