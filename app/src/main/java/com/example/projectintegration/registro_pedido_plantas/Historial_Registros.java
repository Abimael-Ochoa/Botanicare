package com.example.projectintegration.registro_pedido_plantas;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectintegration.R;
import com.example.projectintegration.adapter.PlantOrderAdapter;
import com.example.projectintegration.models.PlantOrder;
import com.example.projectintegration.utilities.Utils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Historial_Registros extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlantOrderAdapter adapter;
    private List<PlantOrder> plantOrders;
    private List<PlantOrder> plantOrdersFiltered; // Lista filtrada para búsqueda
    private EditText etBuscar;
    private Button btnBuscar;
    private Button btnRegresar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_registros);

        Utils.changeStatusBarColor(this, R.color.tu_color_verde);

        recyclerView = findViewById(R.id.rv_historial_pedidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializamos las listas
        plantOrders = new ArrayList<>();
        plantOrdersFiltered = new ArrayList<>();

        // Inicializamos la vista y el adaptador
        etBuscar = findViewById(R.id.et_buscar);
        btnBuscar = findViewById(R.id.btn_buscar);
        btnRegresar = findViewById(R.id.btn_regresar);

        // Configurar el botón "Regresar"
        btnRegresar.setOnClickListener(v -> {
            // Finaliza la actividad actual para regresar a la anterior
            finish();
        });

        adapter = new PlantOrderAdapter(plantOrders, new PlantOrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int orderCode) {
                // Al hacer clic en un item, pasamos el orderCode a la siguiente actividad
                Intent intent = new Intent(Historial_Registros.this, Ticket_Pedido.class);
                intent.putExtra("orderCode", orderCode);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        // Consultamos los pedidos en Firestore
        consultarPedidos();

        // Configurar el botón de búsqueda
        btnBuscar.setOnClickListener(v -> buscarPedidos(etBuscar.getText().toString()));

        // Opción de búsqueda en tiempo real
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // No necesitamos hacer nada aquí
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Cuando el texto cambia, filtramos la lista
                buscarPedidos(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No necesitamos hacer nada aquí
            }
        });
    }

    private void consultarPedidos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("plantOrders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            int orderCode = document.getLong("orderCode").intValue();
                            boolean status = document.getBoolean("status");
                            Date timestamp = document.getDate("timestamp");
                            String clientName = document.getString("cliente.name");

                            // Crear el objeto PlantOrder
                            PlantOrder plantOrder = new PlantOrder(orderCode, new ArrayList<>(), new PlantOrder.Cliente(clientName), status, timestamp);

                            // Agregar el pedido a la lista original
                            plantOrders.add(plantOrder);
                        }

                        // Al obtener todos los pedidos, también llenamos la lista filtrada
                        plantOrdersFiltered.addAll(plantOrders);

                        // Notificar al adaptador que los datos han cambiado
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error al obtener los registros.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Función para filtrar los pedidos según el texto de búsqueda
    private void buscarPedidos(String query) {
        plantOrdersFiltered.clear();

        if (query.isEmpty()) {
            plantOrdersFiltered.addAll(plantOrders); // Si no hay filtro, mostramos todos los pedidos
        } else {
            for (PlantOrder plantOrder : plantOrders) {
                String orderCodeString = String.valueOf(plantOrder.getOrderCode());
                String clientName = plantOrder.getCliente().getName().toLowerCase();

                // Filtramos por ID o nombre de cliente (case insensitive)
                if (orderCodeString.contains(query) || clientName.contains(query.toLowerCase())) {
                    plantOrdersFiltered.add(plantOrder);
                }
            }
        }

        // Notificar al adaptador que los datos han cambiado
        adapter.notifyDataSetChanged();
    }
}

