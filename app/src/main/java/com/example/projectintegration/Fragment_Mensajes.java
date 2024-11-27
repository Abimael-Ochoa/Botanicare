package com.example.projectintegration;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Fragment_Mensajes extends Fragment {

    private String userName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtener datos del bundle
        if (getArguments() != null) {
            userName = getArguments().getString("userName");
        }
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__mensajes, container, false);

        // Mostrar el nombre del usuario en la vista
        TextView tvUserName = view.findViewById(R.id.chat_title);
        tvUserName.setText(userName);

        return view;
    }
}
