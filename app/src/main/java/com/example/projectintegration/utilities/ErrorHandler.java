package com.example.projectintegration.utilities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.projectintegration.R;

public class ErrorHandler {

    // Método para aplicar el estilo de error (rojo) en un campo EditText
    public static void setFieldErrorStyle(EditText field, Context context) {
        field.setBackgroundResource(R.drawable.error_field_background); // Fondo rojo
        field.setHintTextColor(context.getResources().getColor(R.color.errorHintColor)); // Placeholder rojo
    }
    public static void setFieldErrorStyleText(View field, Context context) {
        if (field instanceof EditText) {
            EditText editText = (EditText) field;
            // Cambiar color del borde (asumiendo un estilo en drawable)
            Drawable errorDrawable = ContextCompat.getDrawable(context, R.drawable.edittext_error_style);
            editText.setBackground(errorDrawable);

            // Cambiar color del texto
            editText.setTextColor(Color.RED);
        }
    }
    public static void setFieldErrorStyleInt(int enteredQuantity, int availableQuantity, EditText editText, Context context) {
        // Verificar si la cantidad ingresada excede la disponible
        if (enteredQuantity > availableQuantity) {
            // Cambiar el estilo del EditText para mostrar error
            Drawable errorDrawable = ContextCompat.getDrawable(context, R.drawable.edittext_error_style);
            editText.setBackground(errorDrawable);
            editText.setTextColor(Color.RED);

            // Establecer el mensaje de error
            String error = "Cantidad excede el stock disponible (" + availableQuantity + ")";
            editText.setError(error);

            // Limpiar el valor ingresado
            editText.setText("");
        }
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
