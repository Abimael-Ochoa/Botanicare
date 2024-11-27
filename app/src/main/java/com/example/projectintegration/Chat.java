package com.example.projectintegration;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Chat extends AppCompatActivity {

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat); // Cambia al layout correcto para la actividad

        // Obtener datos del Intent
        userName = getIntent().getStringExtra("userName");

        // Mostrar el nombre del usuario en la vista
        TextView tvUserName = findViewById(R.id.chat_title);
        tvUserName.setText(userName);
    }
}
