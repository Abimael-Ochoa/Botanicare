package com.example.projectintegration;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.projectintegration.utilities.CloudinaryService;
import com.example.projectintegration.utilities.RetrofitClient;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.Manifest;


public class EdicionPlantaActivity extends AppCompatActivity {
    private static final int IMAGE_REQUEST_CODE = 1;
    private ImageView plantImage;
    private EditText plantName, plantDescription;
    private TextView quantityText;
    private Button uploadImageButton, saveButton;
    private Uri imageUri;
    private FirebaseFirestore db;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plant);

        // Permission for sdk between 23 and 29
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EdicionPlantaActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }

// Permission storage for sdk 30 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    startActivityIfNeeded(intent, 101);
                } catch (Exception exception) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    startActivityIfNeeded(intent, 101);
                }
            }
        }

        // Inicializar las vistas de la actividad con los IDs originales
        plantImage = findViewById(R.id.plant_image);
        plantName = findViewById(R.id.plant_name);
        plantDescription = findViewById(R.id.plant_description);
        quantityText = findViewById(R.id.quantity_text);
        uploadImageButton = findViewById(R.id.upload_image_button);
        saveButton = findViewById(R.id.save_button);

        // Botón para cargar imagen
        uploadImageButton.setOnClickListener(v -> openImageSelector());

        // Botón para guardar la planta
        saveButton.setOnClickListener(v -> savePlantData());
    }

    // Abrir selector de imagen
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
            plantImage.setImageURI(imageUri); // Mostrar la imagen seleccionada
        }
    }

    // Método para guardar la planta
    private void savePlantData() {
        String name = plantName.getText().toString().trim();
        String description = plantDescription.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertir Uri a File
        File imageFile = new File(getRealPathFromURI(imageUri));
        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestBody);

        // Retrofit para subir la imagen a Cloudinary
        Retrofit retrofit = RetrofitClient.getClient();
        CloudinaryService service = retrofit.create(CloudinaryService.class);

        String uploadPreset = "android_upload"; // Cambia esto con tu upload preset de Cloudinary
        String apiKey = "927378531348724"; // Tu API Key de Cloudinary
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String signature = generateSignature(timestamp); // Firma generada

        Call<ResponseBody> call = service.uploadImage(
                body,
                uploadPreset,
                apiKey,
                timestamp,
                signature
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String cloudinaryUrl = response.body().string(); // Obtener URL desde Cloudinary
                        String imageUrl = new JSONObject(cloudinaryUrl).getString("url");

                        // Guardar los datos de la planta en Firestore
                        Plant plant = new Plant(name, description, quantity, imageUrl);
                        db.collection("plants").document()
                                .set(plant)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(EdicionPlantaActivity.this, "Planta guardada con éxito", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EdicionPlantaActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(EdicionPlantaActivity.this, "Error al guardar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(EdicionPlantaActivity.this, "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EdicionPlantaActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EdicionPlantaActivity.this, "Error al subir la imagen: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Obtener la ruta real de la URI de la imagen
    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        return null;
    }

    // Método para generar la firma (esto debe implementarse según las especificaciones de Cloudinary)
    private String generateSignature(String timestamp) {
        return "generated_signature"; // Implementa la lógica para generar la firma aquí
    }

    // Actualizar el texto de la cantidad
    private void updateQuantityText() {
        quantityText.setText(String.valueOf(quantity));
    }
}
