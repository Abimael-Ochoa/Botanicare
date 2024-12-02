package com.example.projectintegration;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.projectintegration.adapter.AdapterPlantProgress;
import com.example.projectintegration.models.IPlantProgress;

import java.util.ArrayList;
import java.util.List;

public class FragmentPlantProgress extends Fragment {

    public FragmentPlantProgress() {
        // Required empty public constructor
    }

    public static FragmentPlantProgress newInstance(String param1, String param2) {
        FragmentPlantProgress fragment = new FragmentPlantProgress();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plant_progress, container, false);

        // Configurar el botón de retroceso
        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Crear datos estáticos para el adaptador
        List<IPlantProgress> items = new ArrayList<>();
        items.add(new IPlantProgress(R.drawable.ic_plant, "Descripción de la planta 1", "Planta 1"));
        items.add(new IPlantProgress(R.drawable.ic_plant, "Descripción de la planta 2", "Planta 2"));
        items.add(new IPlantProgress(R.drawable.ic_plant, "Descripción de la planta 3", "Planta 3"));

        // Configurar el adaptador y asignarlo al GridView
        GridView gridView = view.findViewById(R.id.plantsGridView);
        AdapterPlantProgress adapter = new AdapterPlantProgress(getContext(), items);
        gridView.setAdapter(adapter);

        // Configurar OnItemClickListener para abrir la actividad
        gridView.setOnItemClickListener((parent, itemView, position, id) -> {
            IPlantProgress selectedPlant = items.get(position);

            // Crear Intent para la nueva actividad
            Intent intent = new Intent(getContext(), GaleriaProgreso.class);
            intent.putExtra("plantName", selectedPlant.getPlantName());
            intent.putExtra("plantDescription", selectedPlant.getDescription());
            intent.putExtra("plantImage", selectedPlant.getImageResId());

            // Iniciar actividad
            startActivity(intent);
        });

        return view;
    }


}
