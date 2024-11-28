package com.example.projectintegration.registro_pedido_plantas;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.projectintegration.R;
import com.example.projectintegration.catalogo_plantas.PantallaCatalogo;

public class RegisterConfirmationAlert extends DialogFragment {
    private int orderCode;

    public static RegisterConfirmationAlert newInstance(int orderCode) {
        RegisterConfirmationAlert fragment = new RegisterConfirmationAlert();
        Bundle args = new Bundle();
        args.putInt("orderCode", orderCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Obtener el orderCode de los argumentos
        if (getArguments() != null) {
            orderCode = getArguments().getInt("orderCode");
        }

        // Inflar el layout personalizado para la alerta
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_alert_historial, null);

        // Establecer el orderCode en el TextView de la alerta
        TextView tvAlertMessage = view.findViewById(R.id.tv_alerta_mensaje);
        tvAlertMessage.setText("N." + orderCode);

        Button btnConfirmar = view.findViewById(R.id.btnconfirmar);
        btnConfirmar.setOnClickListener(v -> {
            // Al presionar "Aceptar", navega a PantallaCatalogo
            if (getActivity() != null) {
                // Cambiar a PantallaCatalogo
                startActivity(new Intent(getActivity(), PantallaCatalogo.class));
                dismiss(); // Cerrar la alerta
            }
        });

        // Crear el diálogo
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(view);
        dialog.setCancelable(false); // Hacer que no se pueda cancelar tocando fuera
        return dialog;
    }
}
