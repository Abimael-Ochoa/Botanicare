package com.example.projectintegration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import com.example.projectintegration.models.Plant;
import com.example.projectintegration.adapter.PlantAdapter;

import java.util.List;

public class Fragment_Content extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Fragment_Content() {
        // Required empty public constructor
    }

    public static Fragment_Content newInstance(String param1, String param2) {
        Fragment_Content fragment = new Fragment_Content();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__catalogo_plantas, container, false);

        // Encuentra el GridView
        GridView plantsGridView = view.findViewById(R.id.plantsGridView);

        // Configura el número de columnas
        plantsGridView.setNumColumns(2);  // Ajuste de columnas para el diseño en cuadrícula

        // Asume que tienes un adaptador y una lista de datos ya definidos
        // Reemplaza YourExistingAdapter y yourDataList con tu adaptador y lista de datos reales
        List<Plant> yourDataList = getDataFromDatabase();  // Método que obtendría datos de la base de datos
        PlantAdapter adapter = new PlantAdapter(getContext(), yourDataList);
        plantsGridView.setAdapter(adapter);

        // Ajuste para que el GridView se expanda completamente dentro del ScrollView
        plantsGridView.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);

        return view;
    }

    // Método ficticio para obtener datos desde la base de datos, reemplázalo con tu implementación real
    private List<Plant> getDataFromDatabase() {
        // Implementa la lógica para obtener la lista de plantas desde tu base de datos
        return null; // Esto debería ser tu lista de datos real
    }
}
