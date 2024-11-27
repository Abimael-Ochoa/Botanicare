package com.example.projectintegration.inicio_sesion;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectintegration.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailField;
    private Button sendButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.tu_color_verde)); // Reemplaza 'tu_color_verde' con el color que desees
        }

        // Referencia al botón Regresar
        Button backButton = findViewById(R.id.backButton);

        // Configurar el OnClickListener para el botón Regresar
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Este código cierra la actividad actual y regresa a la anterior
                finish();
            }
        });

        emailField = findViewById(R.id.emailField);
        sendButton = findViewById(R.id.sendButton);
        mAuth = FirebaseAuth.getInstance();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Por favor ingresa tu correo electrónico.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Enviar el correo para restablecer contraseña
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, "Correo enviado. Revisa tu bandeja de entrada.", Toast.LENGTH_SHORT).show();
                                finish(); // Opcional: cierra la actividad después de enviar el correo
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, "Error al enviar el correo: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
