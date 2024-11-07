package com.example.projectintegration;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Inicializa Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Obtiene las referencias a los EditText
        EditText nameField = findViewById(R.id.nameField);
        EditText phoneField = findViewById(R.id.phoneField);
        EditText addressField = findViewById(R.id.addressField);
        EditText usernameField = findViewById(R.id.usernameField);
        EditText passwordField = findViewById(R.id.passwordField);

        // Botón de registrarse
        Button registerButton = findViewById(R.id.registerButton);

        // Configura el OnClickListener para el botón de registro
        registerButton.setOnClickListener(v -> {
            String name = nameField.getText().toString().trim();
            String phone = phoneField.getText().toString().trim();
            String address = addressField.getText().toString().trim();
            String email = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            // Verifica si algún campo está vacío
            if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Por favor, llena todos los campos.", Toast.LENGTH_SHORT).show();
            } else {
                // Llama a la función para registrar al usuario
                registerUser(email, password, name, phone, address);
            }
        });

        // Configura el OnClickListener para el texto de inicio de sesión
        TextView registerText = findViewById(R.id.loginText);
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Método para registrar al usuario usando Firebase Authentication y guardar datos en Firestore
    private void registerUser(String email, String password, String name, String phone, String address) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Crea un objeto User con los datos del registro
                            User newUser = new User(name, phone, address, email);

                            // Guarda el objeto User en Firestore bajo la colección "users"
                            db.collection("users").document(user.getUid())
                                    .set(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(RegisterActivity.this, "Registro y guardado exitoso", Toast.LENGTH_SHORT).show();
                                        // Redirige al usuario a la actividad principal
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish(); // Finaliza la actividad actual
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RegisterActivity.this, "Error al guardar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // Muestra el mensaje de error
                        Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
