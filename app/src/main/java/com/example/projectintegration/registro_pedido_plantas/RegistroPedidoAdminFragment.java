package com.example.projectintegration.registro_pedido_plantas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.projectintegration.Historial_Registros;
import com.example.projectintegration.R;
import com.example.projectintegration.models.PlantOrder;
import com.example.projectintegration.models.PlantOrderList;
import com.example.projectintegration.utilities.ErrorHandler;
import com.example.projectintegration.utilities.PlantStockValidator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RegistroPedidoAdminFragment extends Fragment {

    private LinearLayout plantList; // Contenedor principal
    private Button btnAddPlant; // Botón para añadir plantas
    private Button btnRegisterOrder; // Botón para registrar el pedido
    private Button btnHistorial;
    private FirebaseFirestore db;
    private PlantStockValidator plantStockValidator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        // Inflar el diseño del fragmento
        View view = inflater.inflate(R.layout.pantalla_registro_pedido_admin, container, false);
        plantStockValidator = new PlantStockValidator(db, getContext()); // Instanciar la clase de validación

        // Inicializa los elementos de la vista
        plantList = view.findViewById(R.id.layout_lista_plantas); // Asigna el contenedor correctamente
        btnAddPlant = view.findViewById(R.id.btn_add_plant);
        btnRegisterOrder = view.findViewById(R.id.btn_register_order); // Botón para registrar el pedido
        btnHistorial = view.findViewById(R.id.btn_historial); // Botón para ver el historial de pedidos

        // Configurar botón de retroceso
        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed(); // Llamar a la acción de retroceso
            }
        });

        // Añadir la primera planta al iniciar el fragmento
        agregarRegistroInicial();

        // Configura el botón para añadir nuevas plantas
        btnAddPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarRegistro();
            }
        });
        btnRegisterOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!plantStockValidator.validarTodoElStock(plantList)) {
                    return; // Detener el flujo si hay errores de stock
                }
                registrarPedido();
            }
        });

        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear el Intent para abrir otra actividad (página)
                Intent intent = new Intent(getContext(), Historial_Registros.class);
                // Iniciar la actividad
                startActivity(intent);
            }
        });

        return view;
    }

    private void agregarRegistroInicial() {
        // Usa el LayoutInflater para inflar el diseño de la nueva fila
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View nuevaPlantaView = inflater.inflate(R.layout.nueva_planta_registro_item, null);

        Spinner spinner = nuevaPlantaView.findViewById(R.id.plantsAvailable);
        EditText cantidadEditText = nuevaPlantaView.findViewById(R.id.et_plant_quantity);

        // Configura el botón de eliminación para la primera planta
        ImageView eliminateRegisteredPlant = nuevaPlantaView.findViewById(R.id.eliminate_registered);
        eliminateRegisteredPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plantList.removeView(nuevaPlantaView);
            }
        });

        // Llenar el Spinner con datos de Firebase
        cargarPlantasSpinner(spinner);
        cantidadEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                plantStockValidator.validarCantidadStock(spinner, cantidadEditText, () -> {
                    // Acción opcional si la validación es exitosa
                    Toast.makeText(getContext(), "Cantidad válida para " + spinner.getSelectedItem(), Toast.LENGTH_SHORT).show();
                });
            }
        });
        // Añade la nueva vista al contenedor principal
        plantList.addView(nuevaPlantaView);
    }


    private void agregarRegistro() {
        // Usa el LayoutInflater para inflar el diseño de la nueva fila
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View nuevaPlantaView = inflater.inflate(R.layout.nueva_planta_registro_item, null);
        ImageView eliminateRegisteredPlant = nuevaPlantaView.findViewById(R.id.eliminate_registered);
        Spinner spinner = nuevaPlantaView.findViewById(R.id.plantsAvailable);
        EditText cantidadEditText = nuevaPlantaView.findViewById(R.id.et_plant_quantity);

        eliminateRegisteredPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Elimina la planta seleccionada de la lista
                plantList.removeView(nuevaPlantaView);
            }
        });

        cargarPlantasSpinner(spinner);

        cantidadEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                plantStockValidator.validarCantidadStock(spinner, cantidadEditText, () -> {
                    // Acción opcional si la validación es exitosa
                    Toast.makeText(getContext(), "Cantidad válida para " + spinner.getSelectedItem(), Toast.LENGTH_SHORT).show();
                });
            }
        });
        // Añade la nueva vista al contenedor principal
        plantList.addView(nuevaPlantaView);


    }

    private void cargarPlantasSpinner(Spinner spinner) {
        db.collection("plants").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> plantNames = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    if (name != null) {
                        plantNames.add(name);
                    }
                }
                // Crear un adaptador para el Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_spinner_item,
                        plantNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }else{
                Toast.makeText(getContext(), "Error al cargar plantas.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void registrarPedido() {
        List<PlantOrderList> plantItems = new ArrayList<>();

        EditText userName = getView().findViewById(R.id.et_nombre_usuario);
        String clientName = userName.getText().toString();

        if (clientName.isEmpty()) {
            ErrorHandler.setFieldErrorStyle(userName, getContext());
            Toast.makeText(getContext(), "Debe ingresar el nombre del usuario.", Toast.LENGTH_SHORT).show();
            return;
        }

        PlantOrder.Cliente cliente = new PlantOrder.Cliente(clientName);

        for (int i = 0; i < plantList.getChildCount(); i++) {
            View plantaView = plantList.getChildAt(i);

            Spinner spinner = plantaView.findViewById(R.id.plantsAvailable);
            EditText cantidadEditText = plantaView.findViewById(R.id.et_plant_quantity);

            if (spinner.getSelectedItem() == null) {
                Toast.makeText(getContext(), "Debe seleccionar una planta en el registro " + (i + 1), Toast.LENGTH_SHORT).show();
                return;
            }

            String plantName = spinner.getSelectedItem().toString();
            String quantityText = cantidadEditText.getText().toString();

            if (quantityText.isEmpty() || Integer.parseInt(quantityText) <= 0) {
                ErrorHandler.setFieldErrorStyle(cantidadEditText, getContext());
                Toast.makeText(getContext(), "Debe ingresar una cantidad válida en el registro " + (i + 1), Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityText);

            // Consulta previa para obtener el documento de la planta
            db.collection("plants")
                    .whereEqualTo("name", plantName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            DocumentSnapshot plantDoc = task.getResult().getDocuments().get(0);

                            // Ahora ejecutamos la transacción
                            db.runTransaction(transaction -> {
                                DocumentSnapshot snapshot = transaction.get(plantDoc.getReference());
                                int stockDisponible = snapshot.getLong("quantity").intValue();

                                if (quantity > stockDisponible) {
                                    throw new FirebaseFirestoreException(
                                            "Stock insuficiente para la planta: " + plantName,
                                            FirebaseFirestoreException.Code.ABORTED
                                    );
                                }

                                // Actualizar stock
                                int nuevoStock = stockDisponible - quantity;
                                transaction.update(plantDoc.getReference(), "quantity", nuevoStock);

                                // Agregar planta al pedido
                                plantItems.add(new PlantOrderList(plantName, quantity));

                                return null; // Retorno de la transacción
                            }).addOnSuccessListener(aVoid -> {
                                if (!plantItems.isEmpty()) {
                                    // Registrar el pedido después de todas las validaciones
                                    int orderCode = generateOrderCode();
                                    Date timestamp = new Date();
                                    PlantOrder plantOrder = new PlantOrder(orderCode, plantItems, cliente, false, timestamp);

                                    db.collection("plantOrders")
                                            .add(plantOrder)
                                            .addOnSuccessListener(documentReference -> {
                                                Toast.makeText(getContext(), "Pedido registrado con éxito.", Toast.LENGTH_SHORT).show();
                                                RegisterConfirmationAlert dialog = RegisterConfirmationAlert.newInstance(orderCode);
                                                dialog.show(getChildFragmentManager(), "RegisterConfirmationAlert");
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getContext(), "Error al registrar el pedido.", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            }).addOnFailureListener(e -> {
                                if (e instanceof FirebaseFirestoreException) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Error inesperado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Error al buscar la planta: " + plantName, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void updateStock(String plantName, int newStock) {
        db.collection("plants")
                .whereEqualTo("name", plantName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Suponiendo que solo existe un documento por planta con el nombre como clave
                        String plantId = task.getResult().getDocuments().get(0).getId();
                        db.collection("plants").document(plantId)
                                .update("quantity", newStock)
                                .addOnSuccessListener(aVoid -> {
                                    // El stock se ha actualizado correctamente
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Error al actualizar el stock de la planta.", Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }
    private void getStockDisponible(String plantName, final StockCallback callback) {
        db.collection("plants")
                .whereEqualTo("name", plantName)
                .get()
                .addOnCompleteListener(task -> {
                    int stockDisponible = 0; // Valor por defecto
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot plantDoc = task.getResult().getDocuments().get(0);
                        stockDisponible = plantDoc.getLong("quantity").intValue();
                    }
                    // Pasamos el valor de stock disponible al callback
                    callback.onStockRetrieved(stockDisponible);
                });
    }

    // Interfaz Callback
    public interface StockCallback {
        void onStockRetrieved(int stockDisponible);
    }

    private int generateOrderCode() {
        // Generar un código único para la orden
        return (int) (Math.random() * 100000); // Solo un ejemplo
    }

}
