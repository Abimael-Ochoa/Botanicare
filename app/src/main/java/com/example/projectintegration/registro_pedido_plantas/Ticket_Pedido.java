package com.example.projectintegration.registro_pedido_plantas;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectintegration.R;
import com.example.projectintegration.adapter.PlantOrderListAdapter;
import com.example.projectintegration.models.PlantOrderList;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Ticket_Pedido extends AppCompatActivity {

    private TextView tvNombreUsuario, tvOrderDate;
    private RecyclerView rvListaPlantas;
    private Button btnRegresar;
    private List<PlantOrderList> plantItems;  // Lista de plantas de este pedido

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_pedido);

        tvNombreUsuario = findViewById(R.id.tv_nombre_usuario);
        tvOrderDate = findViewById(R.id.tv_order_date);
        rvListaPlantas = findViewById(R.id.rv_lista_plantas);
        rvListaPlantas.setLayoutManager(new LinearLayoutManager(this));
        btnRegresar = findViewById(R.id.btn_regresar); // Enlazar el botón

        // Configurar el botón "Regresar"
        btnRegresar.setOnClickListener(v -> {
            // Finaliza la actividad actual para regresar a la anterior
            finish();
        });


        // Obtener el orderCode del Intent
        int orderCode = getIntent().getIntExtra("orderCode", -1);
        if (orderCode != -1) {
            // Buscar la información del pedido con ese orderCode
            consultarDetallesPedido(orderCode);
        }
    }

    private void consultarDetallesPedido(int orderCode) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("plantOrders")
                .whereEqualTo("orderCode", orderCode)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String clientName = document.getString("cliente.name");
                            Date orderDate = document.getDate("timestamp");

                            // Mostrar la información del cliente y la fecha
                            tvNombreUsuario.setText(clientName);
                            tvOrderDate.setText(orderDate.toString());

                            // Obtener las plantas de este pedido (es una lista de mapas)
                            List<Map<String, Object>> plantItemsMap = (List<Map<String, Object>>) document.get("plantItems");

                            if (plantItemsMap != null && !plantItemsMap.isEmpty()) {
                                // Convertir la lista de mapas a una lista de objetos PlantOrderList
                                List<PlantOrderList> plantList = new ArrayList<>();
                                for (Map<String, Object> plantMap : plantItemsMap) {
                                    String plantName = (String) plantMap.get("plantName");
                                    Long quantity = (Long) plantMap.get("quantity");  // Asegúrate de que el tipo coincida con lo que esperas

                                    // Crea el objeto PlantOrderList y agrégalo a la lista
                                    PlantOrderList plant = new PlantOrderList(plantName, quantity.intValue());
                                    plantList.add(plant);
                                }

                                // Inicializar el adaptador con la lista convertida
                                PlantOrderListAdapter plantOrderListAdapter = new PlantOrderListAdapter(plantList);
                                rvListaPlantas.setAdapter(plantOrderListAdapter);
                            } else {
                                Toast.makeText(this, "Este pedido no tiene plantas asociadas.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(this, "Error al obtener los detalles del pedido.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
