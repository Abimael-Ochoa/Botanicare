package com.example.projectintegration.chat_codigo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectintegration.R;
import com.example.projectintegration.adapter.MessageAdapter;
import com.example.projectintegration.catalogo_plantas.PantallaCatalogo;
import com.example.projectintegration.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ChatUser extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText inputMessage;
    private ImageView sendButton;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messageList;

    private String adminID = "BzGePvzWsbh10CXIV9kWVcje4O02"; // ID fijo del administrador
    private String userId; // ID del usuario actual
    private CollectionReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Configuración de vistas
        recyclerView = findViewById(R.id.recycler_view_messages);
        inputMessage = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        // Obtener datos del usuario actual
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Configurar RecyclerView
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, userId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        // Mostrar "Admin" como el nombre del chat
        TextView tvUserName = findViewById(R.id.chat_title);
        tvUserName.setText("Admin");

        setupChatRef();

        // Botón de retroceso
        ImageView btnBack = findViewById(R.id.back_button);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ChatUser.this, PantallaCatalogo.class);
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
        String chatID = adminID + "_" + userId;

        chatRef = FirebaseFirestore.getInstance()
                .collection("chats")
                .document(chatID)
                .collection("messages");

        loadMessages();
    }

    private void loadMessages() {
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
                    }
                });
    }

    private void sendMessage(String content) {
        long timestamp = System.currentTimeMillis();
        Message message = new Message(userId, adminID, content, timestamp);

        chatRef.add(message)
                .addOnSuccessListener(documentReference -> Log.d("Chat", "Mensaje enviado correctamente"))
                .addOnFailureListener(e -> Log.e("Chat", "Error al enviar el mensaje", e));
    }
}
