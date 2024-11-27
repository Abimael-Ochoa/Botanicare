package com.example.projectintegration.registro_pedido_plantas;

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

import com.example.projectintegration.R;
import com.example.projectintegration.models.PlantOrder;
import com.example.projectintegration.models.PlantOrderList;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RegistroPedidoAdminFragment extends Fragment {

    private LinearLayout plantList; // Contenedor principal
    private Button btnAddPlant; // Botón para añadir plantas
    private Button btnRegisterOrder; // Botón para registrar el pedido
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        View view = inflater.inflate(R.layout.pantalla_registro_pedido_admin, container, false);

        // Inicializa los elementos de la vista
        plantList = view.findViewById(R.id.layout_lista_plantas); // Asigna el contenedor correctamente
        btnAddPlant = view.findViewById(R.id.btn_add_plant);
        btnRegisterOrder = view.findViewById(R.id.btn_register_order); // Botón para registrar el pedido


        db = FirebaseFirestore.getInstance();

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
                registrarPedido();
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

        // Desactivar/eliminar el botón de eliminación para esta vista
        ImageView eliminateRegisteredPlant = nuevaPlantaView.findViewById(R.id.eliminate_registered);
        eliminateRegisteredPlant.setVisibility(View.GONE);

        // Llenar el Spinner con datos de Firebase
        cargarPlantasSpinner(spinner);

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
        List<PlantOrderList> plantItems = new ArrayList<>(); // Lista de plantas

        // Itera sobre las plantas que el usuario ha agregado
        for (int i = 0; i < plantList.getChildCount(); i++) {
            View plantaView = plantList.getChildAt(i);

            Spinner spinner = plantaView.findViewById(R.id.plantsAvailable);
            EditText cantidadEditText = plantaView.findViewById(R.id.et_plant_quantity);

            String plantName = spinner.getSelectedItem().toString();
            String quantityText = cantidadEditText.getText().toString();

            if (!quantityText.isEmpty()) {
                int quantity = Integer.parseInt(quantityText);

                // Crear el objeto PlantOrderList
                PlantOrderList plantOrderList = new PlantOrderList(plantName, quantity);
                plantItems.add(plantOrderList);
            } else {
                Toast.makeText(getContext(), "La cantidad no puede estar vacía.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Crear el objeto PlantOrder con todas las plantas
        int orderCode = generateOrderCode(); // Genera un código único para la orden
        PlantOrder plantOrder = new PlantOrder(orderCode, plantItems);

        // Guardar el objeto en Firestore
        db.collection("plantOrders")
                .add(plantOrder)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Pedido registrado con éxito.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al registrar el pedido.", Toast.LENGTH_SHORT).show();
                });
    }

    private int generateOrderCode() {
        // Generar un código único para la orden
        return (int) (Math.random() * 100000); // Solo un ejemplo
    }




}