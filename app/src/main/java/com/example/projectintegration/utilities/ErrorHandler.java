package com.example.projectintegration.utilities;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.projectintegration.R;

public class ErrorHandler {

    // Método para aplicar el estilo de error (rojo) en un campo EditText
    public static void setFieldErrorStyle(EditText field, Context context) {
        field.setBackgroundResource(R.drawable.error_field_background); // Fondo rojo
        field.setHintTextColor(context.getResources().getColor(R.color.errorHintColor)); // Placeholder rojo
    }

    // Método para resetear el estilo de los campos a su estado normal
    public static void resetFieldStyles(Context context, EditText... fields) {
        for (EditText field : fields) {
            field.setBackgroundResource(R.drawable.normal_field_background); // Fondo normal
            field.setHintTextColor(context.getResources().getColor(R.color.normalHintColor)); // Placeholder normal
        }
    }

    // Método para mostrar un mensaje de error en un TextView
    public static void showErrorMessage(TextView errorMessageView, String message) {
        errorMessageView.setText(message);
        errorMessageView.setVisibility(View.VISIBLE); // Muestra el mensaje de error
    }

    // Método para ocultar el mensaje de error
    public static void hideErrorMessage(TextView errorMessageView) {
        errorMessageView.setVisibility(View.GONE); // Oculta el mensaje de error
    }
}
