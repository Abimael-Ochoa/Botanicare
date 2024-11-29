package com.example.projectintegration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectintegration.adapter.MessageAdapter;
import com.example.projectintegration.catalogo_plantas.PantallaCatalogo;
import com.example.projectintegration.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Chat extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText inputMessage;
    private ImageView sendButton;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messageList;

    private String adminID = "6yAyP0KZqrfTwkrUGMAB69sY4QS2";

    private String userName;
    private String currentUserRole; // "admin" o "user"
    private String currentUserId;   // UID del usuario actual
    private CollectionReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("email", "admin@admin.com")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        adminID = queryDocumentSnapshots.getDocuments().get(0).getId();
                    } else {
                        System.err.println("Error: No se encontró un usuario con el correo admin@admin.com");
                    }
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error al buscar el usuario admin: " + e.getMessage());
                });

        // Configuración de vistas
        recyclerView = findViewById(R.id.recycler_view_messages);
        inputMessage = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        // Inicializar variables de usuario y rol
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentUserRole = FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("admin@admin.com") ? "admin" : "user";

        // Obtener el nombre del usuario con el que el admin está chateando, si es el admin
        userName = getIntent().getStringExtra("userName");

        String receiver;
        receiver = getIntent().getStringExtra("userId");


        if (currentUserRole.equals("admin")) {
            String chatID = adminID + "_" + receiver;
            chatRef = FirebaseFirestore.getInstance()
                    .collection("chats")
                    .document(chatID) // Documento específico del usuario
                    .collection("messages");
        } else {
            String chatID = adminID + "_" + currentUserId;
            chatRef = FirebaseFirestore.getInstance()
                    .collection("chats")
                    .document(chatID) // Documento del usuario actual
                    .collection("messages");
        }

        // Configurar RecyclerView
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, currentUserRole);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        // Mostrar el nombre del usuario en la vista
        TextView tvUserName = findViewById(R.id.chat_title);
        tvUserName.setText(currentUserRole.equals("admin") ? userName : "Admin");

        // Escuchar mensajes de Firestore
        loadMessages();

        // Configurar botón de retroceso
        ImageView btnBack = findViewById(R.id.back_button);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(Chat.this, PantallaCatalogo.class);
            startActivity(intent);
            finish();
        });

        // Enviar mensaje
        sendButton.setOnClickListener(v -> {
            String messageContent = inputMessage.getText().toString().trim();
            if (!messageContent.isEmpty()) {
                sendMessage(messageContent);
                inputMessage.setText("");
            }
        });
    }

    private void loadMessages() {
        // Filtrar mensajes directamente desde Firestore
        chatRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@NonNull QuerySnapshot snapshots, @NonNull FirebaseFirestoreException e) {
                        if (e != null) {
                            // Mostrar mensaje de error si algo salió mal
                            System.err.println("Error al cargar los mensajes: " + e.getMessage());
                            return;  // Si ocurre un error, no continuar
                        }

                        // Limpiar la lista de mensajes antes de agregar los nuevos
                        messageList.clear();

                        // Recorrer todos los mensajes y agregarlos a la lista
                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            Message message = document.toObject(Message.class);

                            // Verificar si el mensaje es relevante (debe ser entre el admin y el usuario)
                            if (message != null &&
                                    ((message.getSender().equals(currentUserId) && message.getReceiver().equals(adminID)) ||
                                            (message.getSender().equals(adminID) && message.getReceiver().equals(currentUserId)))) {
                                messageList.add(message);
                            }
                        }

                        // Notificar al adaptador que los datos han cambiado
                        messageAdapter.notifyDataSetChanged();

                        // Asegurarse de que el RecyclerView se desplace hacia el último mensaje
                        if (!messageList.isEmpty()) {
                            recyclerView.scrollToPosition(messageList.size() - 1);
                        }
                    }
                });
    }





    private void sendMessage(String content) {
        // Validar que el contenido no esté vacío
        if (content == null || content.trim().isEmpty()) {
            System.err.println("Error: El mensaje no puede estar vacío");
            return;
        }

        long timestamp = System.currentTimeMillis();
        String sender = currentUserId; // Siempre usamos el UID único del usuario actual como sender


        if (currentUserRole.equals("admin")) {
            String receiver;
            receiver = getIntent().getStringExtra("userId"); // ID del usuario con el que el admin está chateando
            if (receiver == null || receiver.isEmpty()) {
                System.err.println("Error: No se pudo determinar el receptor para este mensaje.");
                return;
            }
            sendToFirestore(sender, receiver, content, timestamp);
        } else {
            // Buscar el ID del admin con el correo admin@admin.com
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .whereEqualTo("email", "admin@admin.com")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String receiver = queryDocumentSnapshots.getDocuments().get(0).getId();
                            sendToFirestore(sender, receiver, content, timestamp);
                        } else {
                            System.err.println("Error: No se encontró un usuario con el correo admin@admin.com");
                        }
                    })
                    .addOnFailureListener(e -> {
                        System.err.println("Error al buscar el usuario admin: " + e.getMessage());
                    });
        }
    }

    private void sendToFirestore(String sender, String receiver, String content, long timestamp) {
        // Crear el mensaje
        Message message = new Message(sender, receiver, content, timestamp);

        // Enviar el mensaje a Firestore
        chatRef.add(message)
                .addOnSuccessListener(documentReference -> {
                    System.out.println("Mensaje enviado correctamente: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error al enviar el mensaje: " + e.getMessage());
                });
    }



}
