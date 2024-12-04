package com.example.projectintegration;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectintegration.inicio_sesion.LoginScreen;
import com.example.projectintegration.models.Plant;
import com.example.projectintegration.utilities.ErrorHandler;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SubirPlantaActivity extends AppCompatActivity {
    private static final int IMAGE_REQUEST_CODE = 1;

    private ImageView plantImage;
    private EditText plantName, plantDescription;
    private TextView quantityText;
    private Button uploadImageButton, saveButton;
    private Uri imageUri;
    TextView errorMessage;
    EditText plantScientificName;
    EditText  plantCare;
    private FirebaseFirestore db;

    private int quantity = 1;

    private static final String IMGBB_API_URL = "https://api.imgbb.com/1/upload";
    private static final String IMGBB_API_KEY = "2a3854d4c56935ed6fa743b1abee822c"; // Asegúrate de que esta clave es válida y segura.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plant);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.tu_color_verde)); // Reemplaza 'tu_color_verde' con el color que desees
        }

        db = FirebaseFirestore.getInstance();

        plantScientificName = findViewById(R.id.scientific_name);
        plantCare = findViewById(R.id.plant_care);

        // Inicializar vistas
        plantImage = findViewById(R.id.plant_image);
        plantName = findViewById(R.id.plant_name);
        plantDescription = findViewById(R.id.plant_description);
        quantityText = findViewById(R.id.quantity_text);
        uploadImageButton = findViewById(R.id.upload_image_button);
        saveButton = findViewById(R.id.save_button);
        errorMessage = findViewById(R.id.errorMessage);

        uploadImageButton.setOnClickListener(v -> openImageSelector());
        saveButton.setOnClickListener(v -> savePlantData());

        updateQuantityText();

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
        String scientificName = plantScientificName.getText().toString().trim();
        String careDetails = plantCare.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || scientificName.isEmpty() || careDetails.isEmpty() || imageUri == null) {
            ErrorHandler.showErrorMessage(errorMessage, "Completa todos los campos");
            return;
        }

        uploadImageToImgbb(imageUri, url -> {
            Plant plant = new Plant(name, description, quantity, url, scientificName, careDetails);

            db.collection("plants").document()
                    .set(plant)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(SubirPlantaActivity.this, "Planta guardada con éxito", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SubirPlantaActivity.this, LoginScreen.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> ErrorHandler.showErrorMessage(errorMessage, "Error al guardar planta"));
        });
    }

    private void uploadImageToImgbb(Uri imageUri, ImageUploadCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // Leer la imagen y convertirla a Base64
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                // Crear el cliente OkHttp
                OkHttpClient client = new OkHttpClient();

                // Construir el cuerpo de la solicitud
                RequestBody requestBody = new FormBody.Builder()
                        .add("key", IMGBB_API_KEY)
                        .add("image", encodedImage)
                        .build();

                // Crear la solicitud
                Request request = new Request.Builder()
                        .url(IMGBB_API_URL)
                        .post(requestBody)
                        .build();

                // Enviar la solicitud
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    // Parsear la respuesta JSON
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String imageUrl = jsonResponse.getJSONObject("data").getString("url");

                    // Llamar al callback con la URL de la imagen
                    runOnUiThread(() -> callback.onUploadSuccess(imageUrl));
                } else {
                    // Manejo de errores
                    String errorMess = response.body() != null ? response.body().string() : "Error desconocido";
                    Log.e("ImgbbUpload", "Error en la respuesta: " + errorMessage);
                    runOnUiThread(() ->  ErrorHandler.showErrorMessage(errorMessage, "Error al subir imagen"));
                }
            } catch (Exception e) {
                Log.e("ImgbbUpload", "Error al subir la imagen", e);
                runOnUiThread(() -> ErrorHandler.showErrorMessage(errorMessage, "Error al subir imagen"));
            }
        });
    }

    private void updateQuantityText() {
        quantityText.setText(String.valueOf(quantity));
    }

    interface ImageUploadCallback {
        void onUploadSuccess(String imageUrl);
    }
}
