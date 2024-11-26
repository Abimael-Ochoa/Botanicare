package com.example.projectintegration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Mensajes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Mensajes extends Fragment {

    // Parámetros
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Fragment_Mensajes() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Mensajes.
     */
    public static Fragment_Mensajes newInstance(String param1, String param2) {
        Fragment_Mensajes fragment = new Fragment_Mensajes();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.fragment__mensajes, container, false);

        // Referencia al botón de regreso
        ImageView backButton = view.findViewById(R.id.back_button);
        if (backButton != null) {
            // Configurar el OnClickListener para el botón de regreso
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Volver a la pantalla del catálogo (suponiendo que Catalogo es la actividad principal)
                    getActivity().onBackPressed();  // Llama al método onBackPressed() de la actividad
                }
            });
        }

        return view;
    }
}