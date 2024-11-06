package com.example.projectintegration;

import android.os.Bundle;
import android.widget.Button; // Importa la clase Button
import android.widget.EditText; // Importa la clase EditText
import android.widget.TextView; // Importa la clase TextView
import android.widget.Toast; // Importa la clase Toast
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Obtiene las referencias a los EditText
        EditText nameField = findViewById(R.id.nameField);
        EditText phoneField = findViewById(R.id.phoneField);
        EditText addressField = findViewById(R.id.addressField);
        EditText usernameField = findViewById(R.id.usernameField);
        EditText passwordField = findViewById(R.id.passwordField);

        // Botón de registrarse
        Button registerButton = findViewById(R.id.registerButton);

        // Configura el OnClickListener
        registerButton.setOnClickListener(v -> {
            // Obtiene los textos de los campos
            String name = nameField.getText().toString().trim();
            String phone = phoneField.getText().toString().trim();
            String address = addressField.getText().toString().trim();
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            // Verifica si algún campo está vacío
            if (name.isEmpty() || phone.isEmpty() || address.isEmpty() ||
                    username.isEmpty() || password.isEmpty()) {
                // Muestra un mensaje de advertencia
                Toast.makeText(RegisterActivity.this, "Por favor, llena todos los campos.", Toast.LENGTH_SHORT).show();
            } else {
                // Crea un Intent para iniciar la nueva actividad
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent); // Inicia la actividad
            }
        });

        // Configura el OnClickListener para el texto de inicio de sesión
        TextView registerText = findViewById(R.id.loginText);
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
