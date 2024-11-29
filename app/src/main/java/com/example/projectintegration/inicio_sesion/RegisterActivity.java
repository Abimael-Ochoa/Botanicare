package com.example.projectintegration.inicio_sesion;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectintegration.R;
import com.example.projectintegration.models.User;
import com.example.projectintegration.utilities.ErrorHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializa Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Obtiene las referencias a los EditText y TextView de error
        EditText nameField = findViewById(R.id.nameField);
        EditText phoneField = findViewById(R.id.phoneField);
        EditText addressField = findViewById(R.id.addressField);
        EditText usernameField = findViewById(R.id.usernameField);
        EditText passwordField = findViewById(R.id.passwordField);
        errorMessage = findViewById(R.id.errorMessage);

        // Botón de registrarse
        Button registerButton = findViewById(R.id.registerButton);

        // Configura el OnClickListener para el botón de registro
        registerButton.setOnClickListener(v -> {
            String name = nameField.getText().toString().trim();
            String phone = phoneField.getText().toString().trim();
            String address = addressField.getText().toString().trim();
            String email = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            // Validación de campos
            if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || email.isEmpty() || password.isEmpty()) {
                ErrorHandler.showErrorMessage(errorMessage, "Por favor, llena todos los campos.");
                if (name.isEmpty()) {
                    ErrorHandler.setFieldErrorStyle(nameField, RegisterActivity.this);
                }
                if (phone.isEmpty()) {
                    ErrorHandler.setFieldErrorStyle(phoneField, RegisterActivity.this);
                }
                if (address.isEmpty()) {
                    ErrorHandler.setFieldErrorStyle(addressField, RegisterActivity.this);
                }
                if (email.isEmpty()) {
                    ErrorHandler.setFieldErrorStyle(usernameField, RegisterActivity.this);
                }
                if (password.isEmpty()) {
                    ErrorHandler.setFieldErrorStyle(passwordField, RegisterActivity.this);
                }
            } else {
                // Llama a la función para registrar al usuario
                registerUser(email, password, name, phone, address);
                // Resetea los estilos si no hay errores
                ErrorHandler.resetFieldStyles(RegisterActivity.this, nameField, phoneField, addressField, usernameField, passwordField);
                ErrorHandler.hideErrorMessage(errorMessage);
            }
        });

        // Configura los TextWatcher para los campos
        setTextWatcher(nameField);
        setTextWatcher(phoneField);
        setTextWatcher(addressField);
        setTextWatcher(usernameField);
        setTextWatcher(passwordField);

        // Configura el OnClickListener para el texto de inicio de sesión
        TextView registerText = findViewById(R.id.loginText);
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginScreen.class);
            startActivity(intent);
        });
    }

    private void setTextWatcher(EditText field) {
        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Limpia el estilo de error cuando el usuario comienza a escribir
                ErrorHandler.resetFieldStyles(RegisterActivity.this, field);
                errorMessage.setVisibility(View.GONE); // Oculta el mensaje de error
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Método para registrar al usuario usando Firebase Authentication y Firestore
    private void registerUser(String email, String password, String name, String phone, String address) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Crea un objeto User con los datos del registro
                            User newUser = new User(name, phone, address, email,0);


                            // Guarda el objeto User en Firestore bajo la colección "users"
                            db.collection("users").document(user.getUid())
                                    .set(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                        // Redirige al usuario a la actividad principal
                                        Intent intent = new Intent(RegisterActivity.this, LoginScreen.class);
                                        startActivity(intent);
                                        finish(); // Finaliza la actividad actual
                                    })
                                    .addOnFailureListener(e -> {
                                        ErrorHandler.showErrorMessage(errorMessage, "Error al guardar en Firestore");
                                    });
                        }
                    } else {
                        ErrorHandler.showErrorMessage(errorMessage, "El email o la contraseña son incorrectos");
                    }
                });
    }
}
