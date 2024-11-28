package com.example.projectintegration;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_plant_progress, container, false);

        // Encontrar el GridView
        GridView gridView = view.findViewById(R.id.plantsGridView);

        // Crear datos para el adaptador
        List<IPlantProgress> items = new ArrayList<>();
        items.add(new IPlantProgress(R.drawable.ic_plant, "Plant 1"));
        items.add(new IPlantProgress(R.drawable.ic_plant, "Plant 2"));
        items.add(new IPlantProgress(R.drawable.ic_plant, "Plant 3"));
        // Agrega más elementos según sea necesario

        // Configurar el adaptador
        AdapterPlantProgress adapter = new AdapterPlantProgress(getContext(), items);
        gridView.setAdapter(adapter);

        return view;
    }
}
