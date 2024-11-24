package com.example.projectintegration;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
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

import com.example.projectintegration.utilities.ErrorHandler;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.projectintegration.R;
import android.text.TextWatcher;
import android.text.Editable;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;  // Instancia de FirebaseAuth
    TextView errorMessage;
    EditText emailField;
    EditText passwordField;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        // Inicializar FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Referencia al TextView para mostrar los mensajes de error
         errorMessage = findViewById(R.id.errorMessage);

        // Referencias a los campos de entrada de texto (correo y contraseña)
         emailField = findViewById(R.id.usernameField);
         passwordField = findViewById(R.id.passwordField);

        // Referencia al botón de login
        Button enterButton = findViewById(R.id.enterButton);
        enterButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            // Restablece el estado inicial de los campos
            ErrorHandler.resetFieldStyles(MainActivity.this,passwordField,emailField);

            // Verifica que los campos no estén vacíos
            if (email.isEmpty() || password.isEmpty()) {
                errorMessage.setText("Por favor, ingresa correo y contraseña.");
                errorMessage.setVisibility(View.VISIBLE);

                if (email.isEmpty()) {
                    ErrorHandler.setFieldErrorStyle(emailField,this);

                }
                if (password.isEmpty()) {
                    ErrorHandler.setFieldErrorStyle(passwordField,this);
                }
            } else {
                errorMessage.setVisibility(View.GONE);
                // Llama al método de login
                loginUser(email, password);
            }
        });

        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Limpia el estilo de error cuando el usuario comienza a escribir
                ErrorHandler.resetFieldStyles(MainActivity.this,emailField);
                errorMessage.setVisibility(View.GONE); // Oculta el mensaje de error
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Limpia el estilo de error cuando el usuario comienza a escribir
                ErrorHandler.resetFieldStyles(MainActivity.this,passwordField);
                errorMessage.setVisibility(View.GONE); // Oculta el mensaje de error
            }

            @Override
            public void afterTextChanged(Editable s) {}
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
                            ErrorHandler.setFieldErrorStyle(emailField,this);
                            ErrorHandler.setFieldErrorStyle(passwordField,this);
                        // Muestra el error en el TextView
                        String error = "Tu email o contrasena son incorrectos";
                        errorMessage.setText(error);
                        errorMessage.setVisibility(View.VISIBLE);
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
