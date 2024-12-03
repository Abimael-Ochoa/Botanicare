package com.example.projectintegration.Papelera;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.projectintegration.R;
import com.google.firebase.firestore.FirebaseFirestore;


public class FragmentPlantMonitoring extends Fragment {

    private String plantName;
    private String uniqueId;

    private ImageView plantImage;
    private EditText apodoEditText, notasEditText;
    private Button uploadProgressButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plant_monitoring, container, false);

        // Obtener los datos pasados desde FragmentPlantProgress
        if (getArguments() != null) {
            plantName = getArguments().getString("plantName");
            uniqueId = getArguments().getString("uniqueId");
        }

        // Inicializar los elementos de la UI
        plantImage = view.findViewById(R.id.plantImage);
        apodoEditText = view.findViewById(R.id.editTextApodoPlanta);
        notasEditText = view.findViewById(R.id.notas);
        uploadProgressButton = view.findViewById(R.id.upload_image_button);

        // Cargar la imagen de la planta usando el nombre o uniqueId
        loadPlantImage(plantName);

        // Configurar el botón para subir el progreso
        uploadProgressButton.setOnClickListener(v -> {
            String apodo = apodoEditText.getText().toString();
            String notas = notasEditText.getText().toString();

            // Aquí puedes realizar una actualización en la base de datos para guardar el apodo y las notas
            savePlantProgress(apodo, notas);
        });

        return view;
    }

    private void loadPlantImage(String plantName) {
        FirebaseFirestore.getInstance().collection("plants")
                .whereEqualTo("name", plantName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        String imageUrl = querySnapshot.getDocuments().get(0).getString("imageUrl");
                        if (imageUrl != null) {
                            // Cargar la imagen (puedes usar Volley o Glide aquí)
                            Glide.with(this).load(imageUrl).into(plantImage);
                        } else {
                            // Imagen por defecto si no existe URL
                            plantImage.setImageResource(R.drawable.ic_plant);
                        }
                    }
                });
    }

    private void savePlantProgress(String apodo, String notas) {
        FirebaseFirestore.getInstance().collection("plants")
                .document(uniqueId)
                .update(
                        "nickname", apodo,
                        "notes", notas
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Progreso guardado", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al guardar progreso", Toast.LENGTH_SHORT).show();
                });
    }
}
