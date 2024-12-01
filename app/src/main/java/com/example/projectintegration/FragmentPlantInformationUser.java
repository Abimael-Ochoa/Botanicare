package com.example.projectintegration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.projectintegration.R;
import com.squareup.picasso.Picasso;

public class FragmentPlantInformationUser extends Fragment {
    private TextView plantNameTextView;
    private TextView plantDescriptionTextView;
    private TextView plantQuantityTextView; // Nuevo TextView para cantidad
    private ImageView plantImageView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plant_information_user, container, false);

        plantNameTextView = view.findViewById(R.id.plant_name);
        plantDescriptionTextView = view.findViewById(R.id.plant_description);
        plantQuantityTextView = view.findViewById(R.id.quantity_text); // Asocia el TextView
        plantImageView = view.findViewById(R.id.plant_image);

        // Obtener los datos del intent
        Bundle bundle = getArguments();
        if (bundle != null) {
            String plantName = bundle.getString("plantName");
            String plantDescription = bundle.getString("plantDescription");
            String plantImage = bundle.getString("plantImage");
            int plantQuantity = bundle.getInt("plantQuantity"); // Obtener la cantidad

            plantNameTextView.setText(plantName);
            plantDescriptionTextView.setText(plantDescription);
            plantQuantityTextView.setText("Cantidad: " + plantQuantity); // Mostrar cantidad
            if (plantImage != null) {
                Picasso.get().load(plantImage).into(plantImageView);
            }
        }

        return view;
    }
}
