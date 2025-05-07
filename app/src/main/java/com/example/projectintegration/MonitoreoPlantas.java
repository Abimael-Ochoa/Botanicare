package com.example.projectintegration;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectintegration.catalogo_plantas.PantallaCatalogo;
import com.example.projectintegration.models.IPlantProgress;
import com.example.projectintegration.models.Plant;
import com.example.projectintegration.utilities.ErrorHandler;
import com.example.projectintegration.utilities.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.squareup.picasso.Picasso;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MonitoreoPlantas extends AppCompatActivity {

    private ImageView backButton;
    private String plantName;
    private EditText editTextApodoPlanta;
    private TextView tvFecha; // Referencia al TextView de fecha
    private EditText editTextNotas; // Referencia al EditText de notas
    private ImageView plantImage; // Referencia al ImageView de la planta
    private FirebaseFirestore db;
    private String imageUrl; // Variable global para almacenar la URL de la imagen
    private FirebaseUser user;
    private static final int PICK_IMAGE = 1; // Código para el intent de selección de imagen
    private static final String IMGBB_API_URL = "https://api.imgbb.com/1/upload";
    private static final String IMGBB_API_KEY = "2a3854d4c56935ed6fa743b1abee822c"; // Asegúrate de que esta clave es válida y segura.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_monitoreo_plantas);
        db = FirebaseFirestore.getInstance();

        Utils.changeStatusBarColor(this, R.color.tu_color_verde);

        backButton = findViewById(R.id.back_button);
        tvFecha = findViewById(R.id.tv_fecha); // Inicializar el TextView de fecha
        editTextNotas = findViewById(R.id.notas); // Inicializar el EditText de notas
        plantImage = findViewById(R.id.plantImage); // Inicializar el ImageView de la planta

        backButton.setOnClickListener(v -> onBackPressed());

        // Obtener los datos del Intent
        plantName = getIntent().getStringExtra("plantName");
        String uniqueId = getIntent().getStringExtra("uniqueId");
        cargarApodoExistente(uniqueId);



        // Configurar el EditText con el nombre de la planta como placeholder
        try {
            editTextApodoPlanta = findViewById(R.id.editTextApodoPlanta);
            if (plantName != null) {
                editTextApodoPlanta.setHint("Apodo de tu planta (" + plantName + ")");
            }
        }catch (Exception e){
            Toast.makeText(this, "Error en:" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        // Mostrar la fecha actual en el TextView
        showCurrentDate();

        // Configurar el botón de búsqueda de imagen
        findViewById(R.id.buscar).setOnClickListener(v -> openGallery()); // Abrir la galería para seleccionar una imagen

        // Guardar la información en Firebase
        findViewById(R.id.upload_image_button).setOnClickListener(v -> {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                savePlantDataToFirestore(imageUrl);

            } else {
                Toast.makeText(this, "Primero selecciona una imagen.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para mostrar la fecha actual en el formato deseado
    private void showCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
        String currentDate = sdf.format(new Date()); // Obtiene la fecha actual

        // Establecer la fecha en el TextView
        tvFecha.setText("Fecha: " + currentDate);
    }

    // Método para abrir la galería y seleccionar una imagen
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE); // Iniciar el intent para seleccionar la imagen
    }

    // Recibir el resultado de la selección de imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData(); // Obtener la URI de la imagen seleccionada
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                plantImage.setImageBitmap(bitmap); // Establecer la imagen en el ImageView

                // Llamar al método de subida de imagen a Imgbb
                uploadImageToImgbb(imageUri, new ImageUploadCallback() {
                    @Override
                    public void onUploadSuccess(String uploadedImageUrl) {
                        imageUrl = uploadedImageUrl; // Actualizar la variable global
                        savePlantDataToFirestore(imageUrl); // Llamar a guardar los datos
                    }

                    @Override
                    public void onUploadFailure(String error) {
                        // Manejo de errores si la subida falla
                        Log.e("ImageUpload", "Error al subir la imagen: " + error);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Método para subir la imagen a Imgbb
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
                    imageUrl = jsonResponse.getJSONObject("data").getString("url");

                    // Llamar al callback con la URL de la imagen
                } else {
                    // Manejo de errores
                    String errorMess = response.body() != null ? response.body().string() : "Error desconocido";
                    runOnUiThread(() -> callback.onUploadFailure(errorMess));
                }
            } catch (Exception e) {
                Log.e("ImgbbUpload", "Error al subir la imagen", e);
                runOnUiThread(() -> callback.onUploadFailure(e.getMessage()));
            }
        });
    }

    // Método para guardar los datos de la planta en Firestore
    private void savePlantDataToFirestore(String imageUrl) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        // Obtener los valores de los campos de entrada
        String plantNickName = editTextApodoPlanta.getText().toString();
        String notes = editTextNotas.getText().toString();
        String progressDate = tvFecha.getText().toString();
        String uniqueId = getIntent().getStringExtra("uniqueId");

        if (uniqueId == null || uniqueId.isEmpty()) {
            Toast.makeText(this, "No se recibió un ID único válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ 1. Actualizar todos los documentos previos con ese uniqueId
        db.collection("users")
                .document(userId)
                .collection("userPlants")
                .whereEqualTo("uniqueId", uniqueId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        document.getReference().update("plantNickName", plantNickName);
                    }

                    // ✅ 2. Guardar el nuevo registro de progreso
                    Map<String, Object> plantData = new HashMap<>();
                    plantData.put("plantNickName", plantNickName);
                    plantData.put("imageUrl", imageUrl);
                    plantData.put("notes", notes);
                    plantData.put("progressDate", progressDate);
                    plantData.put("uniqueId", uniqueId);

                    String newDocumentId = db.collection("users")
                            .document(userId)
                            .collection("userPlants")
                            .document()
                            .getId();

                    db.collection("users")
                            .document(userId)
                            .collection("userPlants")
                            .document(newDocumentId)
                            .set(plantData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(MonitoreoPlantas.this, "Datos guardados exitosamente.", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(MonitoreoPlantas.this, "Error al guardar los datos.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al actualizar el apodo en documentos anteriores.", Toast.LENGTH_SHORT).show();
                });
    }


    // Interfaz para el callback
    interface ImageUploadCallback {
        void onUploadSuccess(String imageUrl);
        void onUploadFailure(String error);
    }

    private void cargarApodoExistente(String uniqueId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("userPlants")
                .whereEqualTo("uniqueId", uniqueId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Tomamos el apodo más reciente (último documento)
                        String apodo = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1)
                                .getString("plantNickName");

                        if (apodo != null && !apodo.isEmpty()) {
                            editTextApodoPlanta.setText(apodo);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar el apodo", Toast.LENGTH_SHORT).show();
                });
    }

}
