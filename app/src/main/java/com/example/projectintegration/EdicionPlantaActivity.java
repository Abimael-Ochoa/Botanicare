package com.example.projectintegration;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectintegration.R;
import com.example.projectintegration.catalogo_plantas.PantallaCatalogo;
import com.example.projectintegration.inicio_sesion.LoginScreen;
import com.example.projectintegration.models.Plant;
import com.example.projectintegration.utilities.ErrorHandler;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

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

public class EdicionPlantaActivity extends AppCompatActivity {
    private static final int IMAGE_REQUEST_CODE = 1;

    private ImageView plantImage;
    private ImageView backBtn;
    private EditText plantName, plantDescription;
    private EditText quantityText;
    private Button uploadImageButton, saveButton;
    private Uri imageUri;
    TextView errorMessage;

    private EditText scientificName, plantCare;


    private FirebaseFirestore db;

    private int quantity = 1;
    private String plantDocumentId; // Almacenar el ID del documento de la planta

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


        scientificName = findViewById(R.id.scientific_name);
        plantCare = findViewById(R.id.plant_care);
        Button deleteButton = findViewById(R.id.delete_button); // Vincula el botón de eliminar
        deleteButton.setOnClickListener(v -> confirmAndDeletePlant()); // Asigna el método para manejar clics



        // Inicializar vistas
        plantImage = findViewById(R.id.plant_image);
        plantName = findViewById(R.id.plant_name);
        plantDescription = findViewById(R.id.plant_description);
        quantityText = findViewById(R.id.quantity_text);
        uploadImageButton = findViewById(R.id.upload_image_button);
        saveButton = findViewById(R.id.save_button);
        backBtn = findViewById(R.id.back_button);
        errorMessage = findViewById(R.id.errorMessage);

        // Recuperar los datos enviados desde la actividad anterior (PlantInformationActivity)
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String plantNameStr = bundle.getString("plantName");

            // Realizamos la consulta a Firestore para obtener la planta
            db.collection("plants")
                    .whereEqualTo("name", plantNameStr)  // Buscamos la planta por nombre
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                // Se encuentra la planta
                                Plant plant = task.getResult().getDocuments().get(0).toObject(Plant.class);
                                plantDocumentId = task.getResult().getDocuments().get(0).getId(); // Obtener el ID del documento

                                // Setear los datos en los campos
                                plantName.setText(plant.getName());
                                plantDescription.setText(plant.getDescription());
                                quantity = plant.getQuantity();
                                scientificName.setText(plant.getScientificName());
                                plantCare.setText(plant.getCare());
                                updateQuantityText();

                                // Cargar la imagen usando Picasso
                                if (plant.getImageUrl() != null && !plant.getImageUrl().isEmpty()) {
                                    Picasso.get().load(plant.getImageUrl()).into(plantImage);
                                }
                            } else {
                                ErrorHandler.showErrorMessage(errorMessage, "Planta no encontrada");
                            }
                        } else {
                            ErrorHandler.showErrorMessage(errorMessage, "Error al consultar la planta");
                        }
                    });
        }

        uploadImageButton.setOnClickListener(v -> openImageSelector());
        saveButton.setOnClickListener(v -> savePlantData());

        updateQuantityText();
        quantityText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No necesitas implementar esto
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No necesitas implementar esto
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().trim();
                if (!text.isEmpty()) {
                    try {
                        int newQuantity = Integer.parseInt(text);
                        if (newQuantity > 0) {
                            quantity = newQuantity; // Actualizar la variable `quantity`
                        } else {
                            quantityText.setError("La cantidad debe ser mayor a 0");
                        }
                    } catch (NumberFormatException e) {
                        quantityText.setError("Ingresa un número válido");
                    }
                }
            }
        });

        findViewById(R.id.increment_button).setOnClickListener(v -> {
            quantity++;
            updateQuantityText();
        });

        findViewById(R.id.decrement_button).setOnClickListener(v -> {
            if (quantity > 1) quantity--;
            updateQuantityText();
        });

        backBtn.setOnClickListener(v -> onBackPressed()); // Llamar al método de retroceso del Activity


    }



    private void confirmAndDeletePlant() {
        // Mostrar el dialogo de confirmación antes de cerrar sesión
        // Inflar el layout personalizado
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_alert_borrar, null);



        // Crear el AlertDialog con el tema personalizado
        AlertDialog.Builder builder = new AlertDialog.Builder(EdicionPlantaActivity.this, R.style.TransparentDialogTheme);

        builder.setView(dialogView);

        // Obtener los botones y otros elementos del layout
        Button btnConfirmar = dialogView.findViewById(R.id.btnconfirmar);
        Button btnCancelar = dialogView.findViewById(R.id.btncancelar);

        // Crear el dialogo
        AlertDialog alertDialog = builder.create();

        // Configurar el botón "Cerrar Sesión"
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            deletePlantFromFirestore();
            }
        });

        // Configurar el botón "Cancelar"
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss(); // Solo cerrar el dialogo, no hacer nada más
            }
        });

        // Mostrar el dialogo
        alertDialog.show();
    }


    private void deletePlantFromFirestore() {
        if (plantDocumentId != null) {
            db.collection("plants").document(plantDocumentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EdicionPlantaActivity.this, "Planta eliminada con éxito", Toast.LENGTH_SHORT).show();
                        // Redirige al catálogo después de eliminar
                        Intent intent = new Intent(EdicionPlantaActivity.this, PantallaCatalogo.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> ErrorHandler.showErrorMessage(errorMessage, "Error al eliminar planta"));
        } else {
            ErrorHandler.showErrorMessage(errorMessage, "No se encontró el ID de la planta para eliminar");
        }
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
        String scientificsName = scientificName.getText().toString().trim();
        String care = plantCare.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || (imageUri == null && plantImage.getDrawable() == null)) {
            ErrorHandler.showErrorMessage(errorMessage, "Completa todos los campos");
            return;
        }

        if (imageUri != null) {
            // Subir la nueva imagen a Imgbb
            uploadImageToImgbb(imageUri, url -> updatePlantInFirestore(name, description, url,scientificsName,care));
        } else {
            // Usar la URL existente
            db.collection("plants").document(plantDocumentId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String existingImageUrl = documentSnapshot.getString("imageUrl");
                            updatePlantInFirestore(name, description, existingImageUrl,scientificsName,care);
                        }
                    })
                    .addOnFailureListener(e -> ErrorHandler.showErrorMessage(errorMessage, "Error al recuperar la imagen existente"));
        }
    }

    private void updatePlantInFirestore(String name, String description, String imageUrl,String scientificName, String care) {
        db.collection("plants").document(plantDocumentId)
                .update(
                        "name", name,
                        "description", description,
                        "quantity", quantity,
                        "imageUrl", imageUrl,
                        "scientificName", scientificName,
                        "care", care

                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EdicionPlantaActivity.this, "Planta actualizada con éxito", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EdicionPlantaActivity.this, PantallaCatalogo.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> ErrorHandler.showErrorMessage(errorMessage, "Error al actualizar planta"));
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

