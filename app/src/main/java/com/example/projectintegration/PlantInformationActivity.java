package com.example.projectintegration;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectintegration.utilities.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class PlantInformationActivity extends AppCompatActivity {
    private TextView plantNameTextView;
    private TextView plantDescriptionTextView;
    private TextView plantQuantityTextView;
    private TextView plantScientificNameTextView; // Nuevo campo
    private TextView plantCareTextView; // Nuevo campo
    private ImageView plantImageView;
    private ImageView editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_information);
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Utils.changeStatusBarColor(this, R.color.tu_color_verde);

        // Inicializa las vistas
        plantNameTextView = findViewById(R.id.plant_name);
        plantDescriptionTextView = findViewById(R.id.plant_description);
        plantQuantityTextView = findViewById(R.id.quantity_text);
        plantScientificNameTextView = findViewById(R.id.scientific_name); // Inicializa
        plantCareTextView = findViewById(R.id.plant_care); // Inicializa
        plantImageView = findViewById(R.id.plant_image);
        editButton = findViewById(R.id.edit);

        if (!"admin@admin.com".equalsIgnoreCase(email)) {
            editButton.setVisibility(View.GONE);
        }

        // Obtener los datos del Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String plantName = bundle.getString("plantName");
            String plantDescription = bundle.getString("plantDescription");
            String plantImage = bundle.getString("plantImage");
            int plantQuantity = bundle.getInt("plantQuantity");
            String scientificName = bundle.getString("scientificName"); // Nuevo dato
            String careDetails = bundle.getString("care"); // Nuevo dato

            // Setear los datos en las vistas
            plantNameTextView.setText(plantName);
            plantDescriptionTextView.setText(plantDescription);
            plantQuantityTextView.setText(String.valueOf(plantQuantity));
            plantScientificNameTextView.setText(scientificName); // Mostrar nombre científico
            plantCareTextView.setText(careDetails); // Mostrar cuidados

            if (plantImage != null) {
                Picasso.get().load(plantImage).into(plantImageView);
            }
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear el Intent para abrir la actividad de edición
                Intent intent = new Intent(PlantInformationActivity.this, EdicionPlantaActivity.class);

                // Obtener los datos de la planta
                String plantName = plantNameTextView.getText().toString();
                String plantDescription = plantDescriptionTextView.getText().toString();
                String plantImage = plantImageView.getDrawable() != null ? ((BitmapDrawable) plantImageView.getDrawable()).getBitmap().toString() : null;
                int plantQuantity = Integer.parseInt(plantQuantityTextView.getText().toString());
                String scientificName = plantScientificNameTextView.getText().toString(); // Nuevo dato
                String careDetails = plantCareTextView.getText().toString(); // Nuevo dato

                // Agregar los datos al Intent
                intent.putExtra("plantName", plantName);
                intent.putExtra("plantDescription", plantDescription);
                intent.putExtra("plantImage", plantImage != null ? plantImage : ""); // Enviar la URL de la imagen
                intent.putExtra("plantQuantity", plantQuantity);
                intent.putExtra("scientificName", scientificName); // Agregar nombre científico
                intent.putExtra("care", careDetails); // Agregar cuidados

                // Iniciar la actividad de edición
                startActivity(intent);
            }
        });

        // Manejar botón de retroceso
        ImageView btnBack = findViewById(R.id.back); // Obtén la referencia al botón de retroceso
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Llamar al método de retroceso del Activity
            }
        });
    }
}
