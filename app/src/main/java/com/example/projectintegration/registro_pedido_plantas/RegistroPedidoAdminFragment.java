package com.example.projectintegration.registro_pedido_plantas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.projectintegration.R;
import com.example.projectintegration.models.PlantOrder;
import com.example.projectintegration.models.PlantOrderList;
import com.example.projectintegration.utilities.ErrorHandler;
import com.example.projectintegration.utilities.PlantStockValidator;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegistroPedidoAdminFragment extends Fragment {

    private LinearLayout plantList; // Contenedor principal
    private  LinearLayout btnAddPlant; // Botón para añadir plantas
    private LinearLayout  btnRegisterOrder; // Botón para registrar el pedido
    private LinearLayout  btnHistorial;
    private FirebaseFirestore db;
    private PlantStockValidator plantStockValidator;
    private List<String> selectedPlants = new ArrayList<>();
    private int maxPlantCount = 0;
    TextView errorMessage;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        // Inflar el diseño del fragmento
        View view = inflater.inflate(R.layout.fragment_registro_plantas, container, false);
        obtenerCantidadMaximaDePlantas(); // Obtén el límite de plantas disponibles
        plantStockValidator = new PlantStockValidator(db, getContext()); // Instanciar la clase de validación


        // Inicializa los elementos de la vista
        plantList = view.findViewById(R.id.layout_lista_plantas); // Asigna el contenedor correctamente
        btnAddPlant = view.findViewById(R.id.btn_add_plant);
        btnRegisterOrder = view.findViewById(R.id.btn_register_order); // Botón para registrar el pedido
        btnHistorial = view.findViewById(R.id.btn_historial); // Botón para ver el historial de pedidos

        // Referencia al TextView para mostrar los mensajes de error
        errorMessage = view.findViewById(R.id.errorMessage);

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
                btnRegisterOrder.setEnabled(false); // ⛔ Evita múltiples clics

                if (!plantStockValidator.validarTodoElStock(plantList)) {
                    btnRegisterOrder.setEnabled(true); // ✅ Rehabilita si hay error de validación

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
        eliminateRegisteredPlant.setVisibility(View.GONE);


        // Llenar el Spinner con datos de Firebase
        cargarPlantasSpinner(spinner, () -> configurarListener(spinner));

        cantidadEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                plantStockValidator.validarCantidadStock(spinner, cantidadEditText, () -> {
                    // Acción opcional si la validación es exitosa
                    String error = "Cantidad válida para " + spinner.getSelectedItem();
                    errorMessage.setText(error);
                    errorMessage.setVisibility(View.VISIBLE);
                });
            }
        });
        // Añade la nueva vista al contenedor principal
        plantList.addView(nuevaPlantaView);
    }


    private void agregarRegistro() {
        if (plantList.getChildCount() >= maxPlantCount) {
            String error = "Has alcanzado el límite máximo de plantas disponibles.";
            errorMessage.setText(error);
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }
        // Usa el LayoutInflater para inflar el diseño de la nueva fila
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View nuevaPlantaView = inflater.inflate(R.layout.nueva_planta_registro_item, null);
        ImageView eliminateRegisteredPlant = nuevaPlantaView.findViewById(R.id.eliminate_registered);
        Spinner spinner = nuevaPlantaView.findViewById(R.id.plantsAvailable);
        EditText cantidadEditText = nuevaPlantaView.findViewById(R.id.et_plant_quantity);

        eliminateRegisteredPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plantList.removeView(nuevaPlantaView);
                actualizarPlantasSeleccionadas();
                actualizarTodosLosSpinners();
            }
        });

        cargarPlantasSpinner(spinner, () -> configurarListener(spinner));

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

    private void cargarPlantasSpinner(Spinner spinner, Runnable onPlantSelected) {
        // Guarda la selección previa (si existe)
        String previousSelection = spinner.getSelectedItem() != null ? spinner.getSelectedItem().toString() : null;

        db.collection("plants").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> plantNames = new ArrayList<>();
                plantNames.add("Selecciona una planta"); // Placeholder inicial

                for (DocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    if (name != null && (!selectedPlants.contains(name) || name.equals(previousSelection))) {
                        plantNames.add(name);
                    }
                }

                // Crear y asignar adaptador
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_spinner_item,
                        plantNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                // Mantén la selección previa si es válida
                if (previousSelection != null && plantNames.contains(previousSelection)) {
                    spinner.setSelection(plantNames.indexOf(previousSelection));
                }

                // Ejecuta la acción si se selecciona algo
                if (onPlantSelected != null) {
                    onPlantSelected.run();
                }
            } else {
                Toast.makeText(getContext(), "Error al cargar plantas.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarListener(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = spinner.getSelectedItem().toString();

                // Si se selecciona algo distinto al placeholder
                if (!selectedItem.equals("Selecciona una planta")) {
                    actualizarPlantasSeleccionadas();
                    actualizarTodosLosSpinners();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });
    }

    private void actualizarPlantasSeleccionadas() {
        selectedPlants.clear();
        for (int i = 0; i < plantList.getChildCount(); i++) {
            View plantaView = plantList.getChildAt(i);
            Spinner spinner = plantaView.findViewById(R.id.plantsAvailable);
            if (spinner.getSelectedItem() != null) {
                String selectedItem = spinner.getSelectedItem().toString();
                if (!selectedItem.equals("Selecciona una planta")) {
                    selectedPlants.add(selectedItem);
                }
            }
        }
    }

    private void actualizarTodosLosSpinners() {
        for (int i = 0; i < plantList.getChildCount(); i++) {
            View plantaView = plantList.getChildAt(i);
            Spinner spinner = plantaView.findViewById(R.id.plantsAvailable);
            cargarPlantasSpinner(spinner, null); // Actualiza cada Spinner individualmente
        }
    }



    private void registrarPedido() {
        List<PlantOrderList> plantItems = new ArrayList<>();
        List<Task<Void>> transactionTasks = new ArrayList<>();

        EditText userName = getView().findViewById(R.id.et_nombre_usuario);
        String clientName = userName.getText().toString();

        if (clientName.isEmpty()) {
            ErrorHandler.setFieldErrorStyle(userName, getContext());
            errorMessage.setText("Debe ingresar el nombre del usuario.");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        PlantOrder.Cliente cliente = new PlantOrder.Cliente(clientName);

        for (int i = 0; i < plantList.getChildCount(); i++) {
            View plantaView = plantList.getChildAt(i);
            Spinner spinner = plantaView.findViewById(R.id.plantsAvailable);
            EditText cantidadEditText = plantaView.findViewById(R.id.et_plant_quantity);

            String plantName = spinner.getSelectedItem().toString();
            String quantityText = cantidadEditText.getText().toString();

            if (plantName.equals("Selecciona una planta") || quantityText.isEmpty() || Integer.parseInt(quantityText) <= 0) {
                ErrorHandler.setFieldErrorStyle(cantidadEditText, getContext());
                errorMessage.setText("Revisa los datos de la planta en el registro " + (i + 1));
                errorMessage.setVisibility(View.VISIBLE);
                return;
            }

            int quantity = Integer.parseInt(quantityText);

            Task<Void> task = db.collection("plants")
                    .whereEqualTo("name", plantName)
                    .get()
                    .continueWithTask(task1 -> {
                        if (!task1.isSuccessful() || task1.getResult().isEmpty()) {
                            throw new Exception("Planta no encontrada: " + plantName);
                        }

                        DocumentSnapshot plantDoc = task1.getResult().getDocuments().get(0);

                        return db.runTransaction(transaction -> {
                            DocumentSnapshot snapshot = transaction.get(plantDoc.getReference());
                            int stockDisponible = snapshot.getLong("quantity").intValue();

                            if (quantity > stockDisponible) {
                                throw new FirebaseFirestoreException("Stock insuficiente para " + plantName,
                                        FirebaseFirestoreException.Code.ABORTED);
                            }

                            int nuevoStock = stockDisponible - quantity;
                            transaction.update(plantDoc.getReference(), "quantity", nuevoStock);
                            synchronized (plantItems) {
                                plantItems.add(new PlantOrderList(plantName, quantity));
                            }
                            return null;
                        });
                    });

            transactionTasks.add(task);
        }

        // Esperar a que todas las tareas finalicen
        Tasks.whenAllComplete(transactionTasks)
                .addOnSuccessListener(tasks -> {
                    if (!plantItems.isEmpty()) {
                        int orderCode = generateOrderCode();
                        Date timestamp = new Date();
                        PlantOrder order = new PlantOrder(orderCode, plantItems, cliente, false, timestamp);

                        db.collection("plantOrders")
                                .add(order)
                                .addOnSuccessListener(ref -> {
                                    Toast.makeText(getContext(), "Pedido registrado con éxito.", Toast.LENGTH_SHORT).show();
                                    RegisterConfirmationAlert dialog = RegisterConfirmationAlert.newInstance(orderCode);
                                    dialog.show(getChildFragmentManager(), "RegisterConfirmationAlert");
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Error al registrar el pedido.", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al validar stock o procesar el pedido: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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

    private void obtenerCantidadMaximaDePlantas() {
        db.collection("plants").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                maxPlantCount = task.getResult().size();
            } else {
                Toast.makeText(getContext(), "Error al obtener el número de plantas.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
