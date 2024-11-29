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

    private String userName;
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
        userName = getIntent().getStringExtra("userName");
        chatRef = FirebaseFirestore.getInstance().collection("chats").document(userName).collection("messages");

        // Configurar RecyclerView
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, "admin"); // El admin es el remitente actual
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        // Mostrar el nombre del usuario en la vista
        TextView tvUserName = findViewById(R.id.chat_title);
        tvUserName.setText(userName);

        // Escuchar mensajes de Firestore
        loadMessages();

        // Configurar botón de retroceso
        ImageView btnBack = findViewById(R.id.back_button);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de login
                Intent intent = new Intent(Chat.this, PantallaCatalogo.class);
                startActivity(intent);
                finish(); // Finalizar la actividad actual
            }
        });

        // Enviar mensaje
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageContent = inputMessage.getText().toString().trim();
                if (!messageContent.isEmpty()) {
                    sendMessage(messageContent);
                    inputMessage.setText("");
                }
            }
        });
    }

    private void loadMessages() {
        chatRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@NonNull QuerySnapshot snapshots, @NonNull FirebaseFirestoreException e) {
                        if (e != null) {
                            return;  // Manejar errores si es necesario
                        }
                        messageList.clear();
                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            Message message = document.toObject(Message.class);
                            messageList.add(message);
                        }
                        messageAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

    private void sendMessage(String content) {
        long timestamp = System.currentTimeMillis();
        Message message = new Message("admin", content, timestamp);
        chatRef.add(message);  // Agrega el mensaje a la colección
    }
}
