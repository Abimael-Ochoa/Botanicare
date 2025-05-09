package com.example.projectintegration;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class PlantInformationAdmin extends AppCompatActivity {
    private TextView plantNameTextView;
    private TextView plantDescriptionTextView;
    private TextView plantQuantityTextView;
    private ImageView plantImageView;
    private ImageView editButton;

    private TextView scientificNameTextView, plantCareTextView;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_information);
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        scientificNameTextView = findViewById(R.id.scientific_name);
        plantCareTextView = findViewById(R.id.plant_care);

        // Inicializa las vistas
        plantNameTextView = findViewById(R.id.plant_name);
        plantDescriptionTextView = findViewById(R.id.plant_description);
        plantQuantityTextView = findViewById(R.id.quantity_text);
        plantImageView = findViewById(R.id.plant_image);
        editButton = findViewById(R.id.edit);
        TextView cantidadQuality = findViewById(R.id.cantidad_disponible);

        if(!"admin@admin.com".equalsIgnoreCase(email)){
            editButton.setVisibility(View.GONE);
        }



        // Obtener los datos del Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String plantName = bundle.getString("plantName");
            String plantDescription = bundle.getString("plantDescription");
            String plantImage = bundle.getString("plantImage");
            String scientificName = bundle.getString("scientificName");
            String care = bundle.getString("care");
            int plantQuantity = bundle.getInt("plantQuantity", 0); // Cantidad predeterminada: 0

            plantNameTextView.setText(plantName);
            plantDescriptionTextView.setText(plantDescription);
            scientificNameTextView.setText(scientificName);
            cantidadQuality.setText("Plantas en posesión");
            plantCareTextView.setText(care);
            plantQuantityTextView.setText(String.valueOf(plantQuantity)); // Mostrar la cantidad


            // Setear los datos en las vistas
            plantNameTextView.setText(plantName);
            plantDescriptionTextView.setText(plantDescription);
            scientificNameTextView.setText(scientificName);
            plantCareTextView.setText(care);

            if (plantImage != null) {
                Picasso.get().load(plantImage)
                        .placeholder(R.drawable.ic_plant)
                        .error(R.drawable.ic_plant)
                        .into(plantImageView);
            }
        }

        // En el onCreate() de tu Activity
        ImageView btnBack = findViewById(R.id.back); // Obtén la referencia al botón de retroceso
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Llamar al método de retroceso del Activity
            }
        });
    }
}