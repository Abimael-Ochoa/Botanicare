package com.example.projectintegration;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;


import com.example.projectintegration.adapter.AdaptarPlantasAdquiridasUsuario;
import com.example.projectintegration.catalogo_plantas.PantallaCatalogo;
import com.example.projectintegration.models.PAdquridasUsuario;

import java.util.ArrayList;
import java.util.List;

public class PlantasAdquiridasUsuario extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plantas_adquiridas_usuario);

        // Configurar ajustes de ventanas
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar botón de retroceso
        ImageView btnBack = findViewById(R.id.btn_back); // Asegúrate de que este ID esté en tu XML
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, PantallaCatalogo.class);
            startActivity(intent);
            finish(); // Cierra esta actividad para regresar a la anterior
        });

        // Referencia al GridView
        GridView gvPlantas = findViewById(R.id.plantsGridView);

        // Lista de plantas adquiridas (datos de ejemplo)
        List<PAdquridasUsuario> plantas = new ArrayList<>();
        plantas.add(new PAdquridasUsuario("Planta araña", "26/08/2023", R.drawable.ic_plant));
        plantas.add(new PAdquridasUsuario("Planta de casa", "01/07/2023", R.drawable.ic_plant));
        plantas.add(new PAdquridasUsuario("Planta araña", "26/11/2019", R.drawable.ic_plant));
        plantas.add(new PAdquridasUsuario("Planta araña", "03/02/2018", R.drawable.ic_plant));

        // Asignar el adaptador al GridView
        AdaptarPlantasAdquiridasUsuario adapter = new AdaptarPlantasAdquiridasUsuario(this, plantas);
        gvPlantas.setAdapter(adapter);
    }

}