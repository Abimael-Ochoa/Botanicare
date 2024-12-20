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

import com.example.projectintegration.NotiUsuario;
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

public class Chat extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText inputMessage;
    private ImageView sendButton;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messageList;

    private String adminID = "BzGePvzWsbh10CXIV9kWVcje4O02"; // ID fijo del administrador
    private String userId; // ID del usuario con el que el admin está chateando
    private String userName; // Nombre del usuario con el que el admin está chateando
    private CollectionReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Configuración de vistas
        recyclerView = findViewById(R.id.recycler_view_messages);
        inputMessage = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        // Obtener datos del Intent
        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");

        // Configurar RecyclerView
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, adminID);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        // Mostrar el nombre del usuario con el que está chateando el administrador
        TextView tvUserName = findViewById(R.id.chat_title);
        tvUserName.setText(userName);

        setupChatRef();

        // Botón de retroceso
        ImageView btnBack = findViewById(R.id.back_button);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(Chat.this, NotiUsuario.class);
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




    private void markUnreadMessagesAsRead() {
        chatRef.whereEqualTo("isRead", false)
                .whereEqualTo("receiver", adminID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        // Marcar como leído solo si el mensaje existía antes de ser actualizado
                        document.getReference().update("isRead", true);
                    }
                })
                .addOnFailureListener(error -> Log.e("Chat", "Error al marcar mensajes como leídos", error));
    }



    private void sendMessage(String content) {
        long timestamp = System.currentTimeMillis();
        Message message = new Message(adminID, userId, content, timestamp,false);
        markUnreadMessagesAsRead();
        chatRef.add(message)
                .addOnSuccessListener(documentReference -> Log.d("Chat", "Mensaje enviado correctamente"))
                .addOnFailureListener(e -> Log.e("Chat", "Error al enviar el mensaje", e));
    }
}
