package com.example.projectintegration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentCalendario extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        // Obtén el ImageView y verifica que no sea nulo
        ImageView backButton = view.findViewById(R.id.regreso_button);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Llamar a onBackPressed para regresar a la MainActivity
                    getActivity().onBackPressed();  // Llama al método onBackPressed() de la actividad
                }
            });
        }

        return view;
    }
}
