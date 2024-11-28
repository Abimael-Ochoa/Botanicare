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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotiUsuario extends AppCompatActivity {

    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    private ArrayList<UserChat> userList;
    private FirebaseAuth mAuth;  // Instancia de FirebaseAuth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti_usuario); // Usa el archivo XML de Activity
        // Inicializar FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Configurar botón de retroceso
        ImageView btnBack = findViewById(R.id.btn_back);
        // Configurar el botón  back
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

        loadUsersFromFirebase();
    }

    private void openChatActivity(UserChat user) {
        Intent intent = new Intent(this, Chat.class);
        intent.putExtra("userName", user.getName());
        intent.putExtra("unreadMessages", user.getUnreadMessages());
        startActivity(intent);
    }

    private void loadUsersFromFirebase() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String name = userSnapshot.child("name").getValue(String.class);
                    Long unreadMessages = userSnapshot.child("unreadMessages").getValue(Long.class);

                    if (name != null && unreadMessages != null) {
                        userList.add(new UserChat(name, unreadMessages.intValue()));
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NotiUsuario.this, "Error loading users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
