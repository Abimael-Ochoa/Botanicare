package com.example.projectintegration.registro_pedido_plantas;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.projectintegration.R;
import com.example.projectintegration.catalogo_plantas.PantallaCatalogo;
import com.example.projectintegration.utilities.ErrorHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistroPedidoUser extends Fragment {

    private EditText etIdPedido;
    private FirebaseFirestore firestore;
    private String userId;
    private String orderCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializa Firestore y obtiene el ID del usuario actual
        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pantalla_registro_pedido_usuario, container, false);

        // Inicializar vistas
        etIdPedido = view.findViewById(R.id.et_id_usuario);
        LinearLayout btnBuscarPedido = view.findViewById(R.id.buscar);
        LinearLayout btnVerMisPlantas= view.findViewById(R.id.btn_verplantas);

        // Configurar botón de retroceso
        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed(); // Llamar a la acción de retroceso
            }
        });

        // Configurar listener del botón
        btnVerMisPlantas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PantallaCatalogo.class);
                startActivity(intent);

            }
        });


        etIdPedido.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Limpia el estilo de error cuando el usuario comienza a escribir
                ErrorHandler.resetFieldStyles(getActivity(),etIdPedido);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Configurar listener del botón
        btnBuscarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderCode = etIdPedido.getText().toString().trim();

                if (TextUtils.isEmpty(orderCode)) {
                    Toast.makeText(getContext(), "Por favor, ingresa un ID de pedido.", Toast.LENGTH_SHORT).show();
                    ErrorHandler.setFieldErrorStyle(etIdPedido, getActivity());
                } else {
                    buscarPedidoEnFirestore(orderCode);
                }
            }
        });

        return view;
    }

    private void buscarPedidoEnFirestore(String orderCodeInput) {
        try {
            // Asegúrate de que el tipo de dato sea el correcto (integer si es necesario)
            int orderCode = Integer.parseInt(orderCodeInput.trim());

            // Realiza la consulta
            firestore.collection("plantOrders")
                    .whereEqualTo("orderCode", orderCode)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);

                            // Log para depuración
                            Log.d("FirestoreDebug", "Documento encontrado: " + document.getData());

                            // Verificar el estado del pedido
                            Boolean status = document.getBoolean("status");
                            if (status != null && !status) {
                                asignarPedidoAlUsuario(document.getId(), (List<Map<String, Object>>) document.get("plantItems"));
                            } else {
                                Toast.makeText(getContext(), "El pedido ya ha sido procesado o no está disponible.", Toast.LENGTH_SHORT).show();
                                ErrorHandler.setFieldErrorStyle(etIdPedido, getActivity());
                            }
                        } else {
                            // Pedido no encontrado
                            Toast.makeText(getContext(), "El pedido no existe.", Toast.LENGTH_SHORT).show();
                            Log.d("FirestoreDebug", "Consulta vacía para orderCode: " + orderCode);
                            ErrorHandler.setFieldErrorStyle(etIdPedido, getActivity());
                        }
                    });
        } catch (NumberFormatException e) {
            // Manejo de error si el código no es un número


            Toast.makeText(getContext(), "El código de pedido debe ser un número válido.", Toast.LENGTH_SHORT).show();
            ErrorHandler.setFieldErrorStyle(etIdPedido, getActivity());
        }
    }



    private void asignarPedidoAlUsuario(String pedidoId, List<Map<String, Object>> plantItems) {
        // Crear un mapa con las plantas para agregar al usuario
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("plantItems", FieldValue.arrayUnion(plantItems.toArray()));

        // Actualizar la colección "users" con las plantas
        firestore.collection("users")
                .document(userId)
                .update(updateData)
                .addOnSuccessListener(aVoid -> {
                    // Actualizamos el estado del pedido
                    actualizarEstadoPedido(pedidoId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al asignar las plantas al usuario.", Toast.LENGTH_SHORT).show();
                });
    }


    private void actualizarEstadoPedido(String pedidoId) {
        // Marcar el pedido como procesado en Firestore
        firestore.collection("plantOrders")
                .document(pedidoId)
                .update("status", true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Pedido asignado exitosamente.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), Ticket_Pedido.class);
                    intent.putExtra("orderCode", Integer.parseInt(orderCode.trim()));
                    startActivity(intent);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al actualizar el estado del pedido.", Toast.LENGTH_SHORT).show();
                });
    }
}
