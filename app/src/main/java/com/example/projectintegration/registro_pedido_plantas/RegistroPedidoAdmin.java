package com.example.projectintegration.registro_pedido_plantas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectintegration.R;

public class RegistroPedidoAdmin extends AppCompatActivity {

    private LinearLayout plantList; // Contenedor principal
    private Button btnAddPlant; // Botón para añadir plantas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_registro_pedido_admin);

        // Inicializa los elementos de la vista
        plantList = findViewById(R.id.layout_lista_plantas);
        btnAddPlant = findViewById(R.id.btn_add_plant);

        // Configura el botón para añadir nuevas plantas
        btnAddPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarNuevaPlanta();
            }
        });
    }

    private void agregarNuevaPlanta() {
        // Usa el LayoutInflater para inflar el diseño de la nueva fila
        LayoutInflater inflater = LayoutInflater.from(this);
        View nuevaPlantaView = inflater.inflate(R.layout.nueva_planta_registro_item, null);

        // Opcional: Inicializa los elementos de la nueva vista si es necesario
        Spinner spinner = nuevaPlantaView.findViewById(R.id.plantsAvailable);
        EditText cantidadEditText = nuevaPlantaView.findViewById(R.id.et_cantidad_planta);

        // Aquí puedes configurar el Spinner o cualquier elemento adicional
        // Por ejemplo, llenar el Spinner con datos de Firebase si es necesario

        // Añade la nueva vista al contenedor principal
        plantList.addView(nuevaPlantaView);
    }
}
