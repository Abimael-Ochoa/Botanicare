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

import com.example.projectintegration.config.SupabaseConfig;
import com.example.projectintegration.services.SupabaseStorageService;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.net.Uri;
import android.util.Log;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EdicionPlantaActivity extends AppCompatActivity {
    private static final int IMAGE_REQUEST_CODE = 1;

    private ImageView plantImage;
    private EditText plantName, plantDescription;
    private TextView quantityText;
    private Button uploadImageButton, saveButton;
    private Uri imageUri;

    private FirebaseFirestore db;

    private int quantity = 1;

    private static Retrofit retrofit = null;

    public static SupabaseStorageService getStorageService() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder().build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(SupabaseConfig.SUPABASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(SupabaseStorageService.class);
    }

    public void uploadImage(Uri imageUri, String imageName) {
        // Convertir la URI en un archivo
        File file = new File(imageUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageName, requestFile);

        // Obtener la instancia del servicio
        SupabaseStorageService service = getStorageService();

        // Crear el encabezado de autorización con la clave de API
        String authHeader = "Bearer " + SupabaseConfig.SUPABASE_API_KEY;

        // Realizar la llamada a la API de Supabase para subir la imagen
        Call<ResponseBody> call = service.uploadImage(
                authHeader,
                SupabaseConfig.BUCKET_NAME,
                body
        );

        // Enviar la solicitud
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Supabase", "Imagen subida con éxito");
                    Toast.makeText(EdicionPlantaActivity.this, "Imagen subida con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("Supabase", "Error al subir la imagen: " + response.errorBody());
                    Toast.makeText(EdicionPlantaActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Supabase", "Error en la subida: ", t);
                Toast.makeText(EdicionPlantaActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }


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

        // Guardar los datos de la planta en Firebase
        Plant plant = new Plant(name, description, quantity, "image_placeholder"); // Cambia image_placeholder con la URL de Firebase si decides usar Firebase Storage para la imagen.

        db.collection("plants").document()
                .set(plant)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EdicionPlantaActivity.this, "Planta guardada con éxito", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EdicionPlantaActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(EdicionPlantaActivity.this, "Error al guardar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateQuantityText() {
        quantityText.setText(String.valueOf(quantity));
    }
}
