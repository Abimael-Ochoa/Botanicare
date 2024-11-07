package com.example.projectintegration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Pantalla_Catalogo extends AppCompatActivity {

    //CUANDO JALES DE LA BASE DE DATOS CREALES MODELOS A LAS ENTIDADES COMO EL QUE HICE DE USER, MAMAGUEVO

    // Declaración de ImageView para el botón de logout
    ImageView logOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_catalogo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa el ImageView como botón de logout
        logOutButton = findViewById(R.id.searchButton); // Asegúrate de que el ID coincide con el de tu XML

        // Configura el OnClickListener para el botón de logout
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cierra la sesión de Firebase
                FirebaseAuth.getInstance().signOut();

                // Muestra un mensaje de confirmación
                Toast.makeText(Pantalla_Catalogo.this, "Cerraste sesión", Toast.LENGTH_SHORT).show();

                // Redirige al usuario a la pantalla de inicio de sesión
                Intent intent = new Intent(Pantalla_Catalogo.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Finaliza la actividad actual para evitar volver atrás
            }
        });
    }
}
