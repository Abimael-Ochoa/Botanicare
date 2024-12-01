// En el archivo PlantInformationActivity.java

package com.example.projectintegration;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class PlantInformationActivity extends AppCompatActivity {
    private TextView plantNameTextView;
    private TextView plantDescriptionTextView;
    private TextView plantQuantityTextView;
    private ImageView plantImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_information);

        // Inicializa las vistas
        plantNameTextView = findViewById(R.id.plant_name);
        plantDescriptionTextView = findViewById(R.id.plant_description);
        plantQuantityTextView = findViewById(R.id.quantity_text);
        plantImageView = findViewById(R.id.plant_image);

        // Obtener los datos del Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String plantName = bundle.getString("plantName");
            String plantDescription = bundle.getString("plantDescription");
            String plantImage = bundle.getString("plantImage");
            int plantQuantity = bundle.getInt("plantQuantity");

            // Setear los datos en las vistas
            plantNameTextView.setText(plantName);
            plantDescriptionTextView.setText(plantDescription);
            plantQuantityTextView.setText(String.valueOf(plantQuantity));
            if (plantImage != null) {
                Picasso.get().load(plantImage).into(plantImageView);
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
