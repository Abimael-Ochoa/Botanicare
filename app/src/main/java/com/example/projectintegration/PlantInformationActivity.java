// En el archivo PlantInformationActivity.java

package com.example.projectintegration;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectintegration.catalogo_plantas.PantallaCatalogo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class PlantInformationActivity extends AppCompatActivity {
    private TextView plantNameTextView;
    private TextView plantDescriptionTextView;
    private TextView plantQuantityTextView;
    private ImageView plantImageView;
    private ImageView editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_information);
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Inicializa las vistas
        plantNameTextView = findViewById(R.id.plant_name);
        plantDescriptionTextView = findViewById(R.id.plant_description);
        plantQuantityTextView = findViewById(R.id.quantity_text);
        plantImageView = findViewById(R.id.plant_image);
        editButton = findViewById(R.id.edit);
        if (!"admin@admin.com".equalsIgnoreCase(email)){
            editButton.setVisibility(View.GONE);
        }


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
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear el Intent para abrir la actividad de edición
                Intent intent = new Intent(PlantInformationActivity.this, EdicionPlantaActivity.class);

                // Obtener los datos de la planta que ya están en los TextViews y la URL de la imagen
                String plantName = plantNameTextView.getText().toString();
                String plantDescription = plantDescriptionTextView.getText().toString();
                String plantImage = plantImageView.getDrawable() != null ? ((BitmapDrawable) plantImageView.getDrawable()).getBitmap().toString() : null;
                int plantQuantity = Integer.parseInt(plantQuantityTextView.getText().toString());

                // Agregar los datos al Intent
                intent.putExtra("plantName", plantName);
                intent.putExtra("plantDescription", plantDescription);
                intent.putExtra("plantImage", plantImage != null ? plantImage : "");  // Enviar la URL de la imagen
                intent.putExtra("plantQuantity", plantQuantity);

                // Iniciar la actividad de edición
                startActivity(intent);
            }
        });



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
