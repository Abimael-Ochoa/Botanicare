package com.example.projectintegration.chat_codigo;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectintegration.R;
import com.example.projectintegration.adapter.MessageAdapter;
import com.example.projectintegration.models.Message;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;

public abstract class BaseChatActivity extends AppCompatActivity {
    protected RecyclerView recyclerView;
    protected MessageAdapter messageAdapter;
    protected ArrayList<Message> messageList;
    protected CollectionReference chatRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Configuración del RecyclerView
        recyclerView = findViewById(R.id.recycler_view_messages);
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, getCurrentUserId());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);
    }

    protected void loadMessages() {
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

    protected abstract String getCurrentUserId(); // Para obtener el ID del usuario actual
}
