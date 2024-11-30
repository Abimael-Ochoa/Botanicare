package com.example.projectintegration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private String adminID = "BzGePvzWsbh10CXIV9kWVcje4O02"; // Este valor se obtiene dinámicamente
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
                        setupChatRef(); // Llamar a la configuración del chatRef después de obtener el adminID
                    } else {
                        Log.e("Chat", "Error: No se encontró el usuario admin.");
                    }
                })
                .addOnFailureListener(e -> Log.e("Chat", "Error al buscar el usuario admin", e));

        // Configuración de vistas
        recyclerView = findViewById(R.id.recycler_view_messages);
        inputMessage = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        // Inicializar variables de usuario y rol
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentUserRole = FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("admin@admin.com") ? "admin" : "user";

        // Obtener el nombre del usuario con el que el admin está chateando, si es el admin
        userName = getIntent().getStringExtra("userName");

        // Configurar RecyclerView
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, currentUserId);
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

    private void setupChatRef() {
        String chatID =   adminID + "_" + getIntent().getStringExtra("userId");;

        chatRef = FirebaseFirestore.getInstance()
                .collection("chats")
                .document(chatID)
                .collection("messages"); // Subcolección de mensajes dentro del chat
        loadMessages();
    }

    private void loadMessages() {
        if (chatRef == null) {
            Log.e("Chat", "chatRef no está inicializado para cargar mensajes.");
            return;
        }

        chatRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("FirestoreError", "Error al cargar los mensajes", e);
                        return;
                    }

                    messageList.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            Message message = document.toObject(Message.class);
                            if (message != null) {
                                messageList.add(message);
                            }
                        }
                        messageAdapter.notifyDataSetChanged();

                        // Desplazar al último mensaje
                        if (!messageList.isEmpty()) {
                            recyclerView.scrollToPosition(messageList.size() - 1);
                        }
                    } else {
                        Log.d("Firestore", "No hay mensajes disponibles.");
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

        String receiver;
        if (currentUserRole.equals("admin")) {
            receiver = getIntent().getStringExtra("userId"); // ID del usuario con el que el admin está chateando
            if (receiver == null || receiver.isEmpty()) {
                System.err.println("Error: No se pudo determinar el receptor para este mensaje.");
                return;
            }
        } else {
            receiver = adminID; // El admin es el receptor cuando el usuario está enviando un mensaje
        }

        sendToFirestore(sender, receiver, content, timestamp);
    }

    private void sendToFirestore(String sender, String receiver, String content, long timestamp) {
        if (chatRef == null) {
            Log.e("Chat", "chatRef no está inicializado para enviar mensajes.");
            return;
        }

        Message message = new Message(sender, receiver, content, timestamp);

        chatRef.add(message)
                .addOnSuccessListener(documentReference -> Log.d("Chat", "Mensaje enviado correctamente: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.e("Chat", "Error al enviar el mensaje", e));
    }
}