package com.example.projectintegration.utilities;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.projectintegration.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PlantStockValidator {

    private final FirebaseFirestore db;
    private final Context context;

    public PlantStockValidator(FirebaseFirestore db, Context context) {
        this.db = db;
        this.context = context;
    }

    // Método para validar una cantidad específica contra el stock
    public void validarCantidadStock(Spinner spinner, EditText cantidadEditText, Runnable onValidationSuccess) {
        String selectedPlant = (String) spinner.getSelectedItem();
        String enteredQuantityText = cantidadEditText.getText().toString();

        if (enteredQuantityText.isEmpty()) return;

        int enteredQuantity = Integer.parseInt(enteredQuantityText);

        db.collection("plants")
                .whereEqualTo("name", selectedPlant)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot plantDoc = task.getResult().getDocuments().get(0);
                        int availableQuantity = plantDoc.getLong("quantity").intValue();

                        if (enteredQuantity > availableQuantity) {
                            cantidadEditText.setError("Cantidad excede el stock disponible (" + availableQuantity + ")");
                            cantidadEditText.setText(""); // Limpia el valor ingresado
                            return;
                        } else {
                            onValidationSuccess.run(); // Ejecuta una acción en caso de validación exitosa
                        }
                    } else {
                        Toast.makeText(context, "Error al verificar stock de la planta.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
    }

    // Método para validar todas las cantidades antes de registrar
    public boolean validarTodoElStock(LinearLayout plantList) {
        for (int i = 0; i < plantList.getChildCount(); i++) {
            View plantaView = plantList.getChildAt(i);
            Spinner spinner = plantaView.findViewById(R.id.plantsAvailable);
            EditText cantidadEditText = plantaView.findViewById(R.id.et_plant_quantity);

            String selectedPlant = (String) spinner.getSelectedItem();
            String enteredQuantityText = cantidadEditText.getText().toString();

            if (enteredQuantityText.isEmpty()) {
                Toast.makeText(context, "Debe ingresar una cantidad válida en el registro " + (i + 1), Toast.LENGTH_SHORT).show();
                return false;
            }

            int enteredQuantity = Integer.parseInt(enteredQuantityText);

            final int registroIndex = i + 1;

            db.collection("plants")
                    .whereEqualTo("name", selectedPlant)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            int availableQuantity = task.getResult().getDocuments().get(0).getLong("quantity").intValue();

                            if (enteredQuantity > availableQuantity) {
                                Toast.makeText(context, "Cantidad en el registro " + registroIndex + " excede el stock disponible (" + availableQuantity + ")", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
        }
        return true;
    }
}
