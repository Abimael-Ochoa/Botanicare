package com.example.projectintegration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectintegration.adapter.UserAdapter;
import com.example.projectintegration.catalogo_plantas.PantallaCatalogo;
import com.example.projectintegration.inicio_sesion.LoginScreen;
import com.example.projectintegration.models.UserChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotiUsuario extends AppCompatActivity {

    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    private ArrayList<UserChat> userList;
    private FirebaseAuth mAuth;  // Instancia de FirebaseAuth
    private CollectionReference usersRef;  // Referencia a la colección de usuarios en Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti_usuario);

        // Inicializar FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Referencia a la colección "users" en Firestore
        usersRef = FirebaseFirestore.getInstance().collection("users");

        // Configurar botón de retroceso
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar sesión en Firebase
                mAuth.signOut();

                // Redirigir a la actividad de login
                Intent intent = new Intent(NotiUsuario.this, LoginScreen.class);
                startActivity(intent);
                finish(); // Finalizar la actividad actual
            }
        });

        rvUsers = findViewById(R.id.rv_users);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);

        userAdapter.setOnUserClickListener(user -> openChatActivity(user));

        rvUsers.setAdapter(userAdapter);

        loadUsersFromFirestore();  // Cambiado para usar Firestore
    }

    private void openChatActivity(UserChat user) {
        Intent intent = new Intent(this, Chat.class);
        intent.putExtra("userName", user.getName());
        intent.putExtra("unreadMessages", user.getUnreadMessages());
        startActivity(intent);
    }

    private void loadUsersFromFirestore() {
        usersRef.addSnapshotListener((QuerySnapshot snapshots, @NonNull FirebaseFirestoreException e) -> {
            if (e != null) {
                Toast.makeText(NotiUsuario.this, "Error loading users", Toast.LENGTH_SHORT).show();
                return;
            }

            userList.clear();
            if (snapshots != null) {
                snapshots.forEach(document -> {
                    String name = document.getString("name");
                    Long unreadMessages = document.getLong("unreadMessages");

                    if (name != null && unreadMessages != null) {
                        userList.add(new UserChat(name, unreadMessages.intValue()));
                    }
                });
            }
            userAdapter.notifyDataSetChanged();
        });
    }
}
