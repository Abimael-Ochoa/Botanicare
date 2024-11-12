package com.example.projectintegration;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EdicionPlantaActivity extends AppCompatActivity {
 //edicion
    private static final int IMAGE_REQUEST_CODE = 1;

    private ImageView plantImage;
    private EditText plantName, plantDescription;
    private TextView quantityText;
    private Button uploadImageButton, saveButton;
    private Uri imageUri;

    private FirebaseFirestore db;

    private int quantity = 1;
    private static final String GITHUB_TOKEN = ""; // Reemplaza con tu token
    private static final String REPO_OWNER = "Abimael-Ochoa"; // Reemplaza con tu usuario de GitHub
    private static final String REPO_NAME = "Botanicare"; // Reemplaza con el nombre del repositorio
    private static final String GITHUB_API_URL = "https://api.github.com/repos/" + REPO_OWNER + "/" + REPO_NAME + "/contents/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plant);

        db = FirebaseFirestore.getInstance();

        // Inicializar vistas
        plantImage = findViewById(R.id.plant_image);
        plantName = findViewById(R.id.plant_name);
        plantDescription = findViewById(R.id.plant_description);
        quantityText = findViewById(R.id.quantity_text);
        uploadImageButton = findViewById(R.id.upload_image_button);
        saveButton = findViewById(R.id.save_button);

        uploadImageButton.setOnClickListener(v -> openImageSelector());
        saveButton.setOnClickListener(v -> savePlantData());

        findViewById(R.id.increment_button).setOnClickListener(v -> {
            quantity++;
            updateQuantityText();
        });

        findViewById(R.id.decrement_button).setOnClickListener(v -> {
            if (quantity > 1) quantity--;
            updateQuantityText();
        });
    }

    private void openImageSelector() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            plantImage.setImageURI(imageUri); // Muestra la imagen seleccionada en el ImageView
        }
    }

    private void savePlantData() {
        String name = plantName.getText().toString().trim();
        String description = plantDescription.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            String imageBase64 = encodeImageToBase64(bitmap);

            // Subir la imagen a GitHub utilizando un hilo de fondo
            String imageName = name + ".png";

            // Ejecutar la tarea de subida en un hilo en segundo plano
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                boolean result = uploadImageToGitHub(imageBase64, imageName);

                // Después de completar la operación de red, volvemos al hilo principal para actualizar la UI
                runOnUiThread(() -> {
                    if (result) {
                        Plant plant = new Plant(name, description, quantity, imageName);

                        db.collection("plants").document()
                                .set(plant)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(EdicionPlantaActivity.this, "Planta guardada con éxito", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EdicionPlantaActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(EdicionPlantaActivity.this, "Error al guardar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(EdicionPlantaActivity.this, "Error al subir la imagen. Los datos no se guardaron.", Toast.LENGTH_SHORT).show();
                    }
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
        }
    }



    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    // Método modificado para subir la imagen usando un hilo en segundo plano
    private boolean uploadImageToGitHub(String imageBase64, String imageName) {
        boolean uploadSuccess = false;

        try {
            URL url = new URL(GITHUB_API_URL + imageName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authorization", "Bearer " + GITHUB_TOKEN);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);

            String jsonPayload = "{ \"message\": \"Subir imagen de planta\", \"content\": \"" + imageBase64 + "\" }";
            OutputStream os = connection.getOutputStream();
            os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == 201) {
                Log.d("GitHubUpload", "Imagen subida con éxito");
                uploadSuccess = true;
            } else {
                Log.d("GitHubUpload", "Error al subir imagen: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("GitHubUpload", "Error al subir imagen", e);
        }

        return uploadSuccess;
    }


    private void updateQuantityText() {
        quantityText.setText(String.valueOf(quantity));
    }
}
