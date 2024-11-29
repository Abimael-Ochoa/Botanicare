package com.example.projectintegration;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.projectintegration.adapter.ConsejoAdapter;
import com.example.projectintegration.models.Consejo;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Consejos extends Fragment {

    private EditText etEscribirConsejo;
    private RecyclerView rvConsejos;
    private ConsejoAdapter consejoAdapter;
    private List<Consejo> listaConsejos;

    public Fragment_Consejos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__consejos, container, false);


        // Configurar botón de retroceso
        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed(); // Llamar a la acción de retroceso
            }
        });

        etEscribirConsejo = view.findViewById(R.id.etEscribirConsejo);
        ImageView btnEnviarConsejo = view.findViewById(R.id.btnEnviarConsejo);
        rvConsejos = view.findViewById(R.id.rvConsejos);

        // Inicializar lista y adaptador
        listaConsejos = new ArrayList<>();
        consejoAdapter = new ConsejoAdapter(listaConsejos);

        // Configurar RecyclerView
        rvConsejos.setLayoutManager(new LinearLayoutManager(getContext()));
        rvConsejos.setAdapter(consejoAdapter);

        // Acción al presionar el botón de enviar
        btnEnviarConsejo.setOnClickListener(v -> {
            String textoConsejo = etEscribirConsejo.getText().toString().trim();
            if (!textoConsejo.isEmpty()) {
                // Agregar nuevo consejo a la lista
                listaConsejos.add(new Consejo("Usuario", textoConsejo, R.drawable.usuario_icon));
                consejoAdapter.notifyItemInserted(listaConsejos.size() - 1);

                // Limpiar el campo de texto
                etEscribirConsejo.setText("");
            }
        });

        return view;
    }
}
