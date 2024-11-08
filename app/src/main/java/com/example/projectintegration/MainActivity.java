package com.example.projectintegration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.projectintegration.R;


public class MainActivity extends AppCompatActivity {



    private FirebaseAuth mAuth;  // Instancia de FirebaseAuth

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        // Inicializar FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Referencias a los campos de entrada de texto (correo y contraseña)
        EditText emailField = findViewById(R.id.usernameField);
        EditText passwordField = findViewById(R.id.passwordField);

        // Referencia al botón de login
        Button enterButton = findViewById(R.id.enterButton);
        enterButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            // Verifica que los campos no estén vacíos
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Por favor, ingresa correo y contraseña.", Toast.LENGTH_SHORT).show();
            } else {
                // Llama al método de login
                loginUser(email, password);
            }
        });

        // Configura el OnClickListener para el texto de registro (si el usuario no tiene cuenta)
        TextView registerText = findViewById(R.id.registerText);
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }



    // Método para iniciar sesión
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Si la autenticación es exitosa, obtiene el usuario actual
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Si el usuario está autenticado, redirige a la pantalla del catálogo
                            Intent catalogIntent = new Intent(MainActivity.this, Pantalla_Catalogo.class);
                            startActivity(catalogIntent);
                            finish(); // Cierra la actividad de login
                        }
                    } else {
                        // Si falla la autenticación, muestra un mensaje de error
                        Toast.makeText(MainActivity.this, "Error de autenticación: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Verifica si el usuario ya está autenticado al iniciar la actividad
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Si el usuario ya está autenticado, redirige directamente al catálogo
            Intent catalogIntent = new Intent(MainActivity.this, Pantalla_Catalogo.class);
            startActivity(catalogIntent);
            finish(); // Cierra la actividad de login
        }
    }


}
