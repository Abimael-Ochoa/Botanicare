// En el archivo PlantInformationActivity.java

package com.example.projectintegration;

import android.os.Bundle;
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
            plantQuantityTextView.setText("Cantidad: " + plantQuantity);
            if (plantImage != null) {
                Picasso.get().load(plantImage).into(plantImageView);
            }
        }
    }
}
