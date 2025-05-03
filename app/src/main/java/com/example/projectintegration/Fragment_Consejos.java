package com.example.projectintegration;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectintegration.adapter.ConsejoAdapter;
import com.example.projectintegration.models.Consejo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Fragment_Consejos extends Fragment {

    private EditText etEscribirConsejo;
    private RecyclerView rvConsejos;
    private ConsejoAdapter consejoAdapter;
    private List<Consejo> listaConsejos;

    // Instancias de Firebase
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    public Fragment_Consejos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__consejos, container, false);

        // Inicializar Firebase
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Configurar botón de retroceso
        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // 1) Encuentra tus vistas
        etEscribirConsejo   = view.findViewById(R.id.etEscribirConsejo);
        TextView tvWarning  = view.findViewById(R.id.tvMaxLengthWarning);
        ImageView btnEnviar = view.findViewById(R.id.btnEnviarConsejo);
        rvConsejos          = view.findViewById(R.id.rvConsejos);

        // 2) Aplica el filtro de longitud en código (opcional si ya lo pusiste en el XML)
        etEscribirConsejo.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(100)
        });

        // 3) Añade el TextWatcher justo después de configurar el filtro
        etEscribirConsejo.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Muestra el aviso cuando llegue a 100 y ocúltalo si es menor
                if (s.length() >= 100) {
                    tvWarning.setVisibility(View.VISIBLE);
                } else {
                    tvWarning.setVisibility(View.GONE);
                }
            }
        });

        // 4) Inicializa RecyclerView y adaptador
        listaConsejos    = new ArrayList<>();
        consejoAdapter   = new ConsejoAdapter(listaConsejos);
        rvConsejos.setLayoutManager(new LinearLayoutManager(getContext()));
        rvConsejos.setAdapter(consejoAdapter);

        // 5) Carga los consejos existentes
        cargarConsejos();

        // 6) Listener del botón de enviar
        btnEnviar.setOnClickListener(v -> enviarConsejo());

        return view;
    }

    private void cargarConsejos() {
        // Leer todos los documentos de la colección "consejos"
        firestore.collection("consejos")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Ordenar por fecha
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaConsejos.clear(); // Limpiar la lista antes de agregar nuevos elementos
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String usuario = document.getString("usuario");
                        String texto = document.getString("texto");
                        listaConsejos.add(new Consejo(usuario, texto, R.drawable.usuario_icon));
                    }
                    consejoAdapter.notifyDataSetChanged(); // Notificar cambios al adaptador
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar consejos", Toast.LENGTH_SHORT).show());
    }

    private void obtenerNombreUsuario(NombreUsuarioCallback callback) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null && user.getEmail() != null) {
            String userEmail = user.getEmail();

            firestore.collection("users")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String nombre = queryDocumentSnapshots.getDocuments().get(0).getString("name");
                            if (nombre != null) {
                                callback.onNombreObtenido(nombre);
                            } else {
                                callback.onError("Nombre no encontrado");
                            }
                        } else {
                            callback.onError("Usuario no encontrado en la base de datos");
                        }
                    })
                    .addOnFailureListener(e -> callback.onError("Error al obtener usuario: " + e.getMessage()));
        } else {
            callback.onError("Usuario no autenticado o correo no disponible");
        }
    }


    private void enviarConsejo() {
        String textoConsejo = etEscribirConsejo.getText().toString().trim();

        if (!TextUtils.isEmpty(textoConsejo)) {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                obtenerNombreUsuario(new NombreUsuarioCallback() {
                    @Override
                    public void onNombreObtenido(String nombre) {
                        // Crear objeto para guardar en Firestore
                        Map<String, Object> consejoMap = new HashMap<>();
                        consejoMap.put("usuario", nombre);
                        consejoMap.put("texto", textoConsejo);
                        consejoMap.put("timestamp", System.currentTimeMillis());

                        // Guardar en Firestore
                        firestore.collection("consejos")
                                .add(consejoMap)
                                .addOnSuccessListener(documentReference -> {
                                    // Agregar a la lista local y actualizar RecyclerView
                                    listaConsejos.add(0, new Consejo(nombre, textoConsejo, R.drawable.usuario_icon));
                                    consejoAdapter.notifyItemInserted(0);
                                    rvConsejos.scrollToPosition(0); // Desplazarse al principio

                                    // Limpiar el campo de texto
                                    etEscribirConsejo.setText("");
                                    Toast.makeText(getContext(), "Consejo enviado", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al enviar consejo", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onError(String mensaje) {
                        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Escribe un consejo antes de enviar", Toast.LENGTH_SHORT).show();
        }
    }
}

    interface NombreUsuarioCallback {
    void onNombreObtenido(String nombre);
    void onError(String mensaje);
}
