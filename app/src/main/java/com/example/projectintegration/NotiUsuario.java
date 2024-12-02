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
import com.example.projectintegration.chat_codigo.Chat;
import com.example.projectintegration.inicio_sesion.LoginScreen;
import com.example.projectintegration.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotiUsuario extends AppCompatActivity {

    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    private ArrayList<User> userList;
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

    private void openChatActivity(User user) {
        Intent intent = new Intent(this, Chat.class);
        intent.putExtra("userName", user.getName());
        intent.putExtra("unreadMessages", user.getUnreadMessages());
        intent.putExtra("userId", user.getId()); // UID del usuario receptor
        startActivity(intent);

        startActivity(intent);
    }

    private void loadUsersFromFirestore() {
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userList.clear(); // Limpiar lista de usuarios
                for (DocumentSnapshot userDoc : task.getResult()) {
                    String userId = userDoc.getId();
                    String name = userDoc.getString("name");

                    // Omitir admin por su ID
                    if ("BzGePvzWsbh10CXIV9kWVcje4O02".equals(userId)) continue;

                    // Crear usuario temporal con mensajes no leídos inicialmente en 0
                    User user = new User(name, 0, userId);

                    // Consultar mensajes no leídos de este usuario
                    FirebaseFirestore.getInstance()
                            .collection("chats")
                            .document("BzGePvzWsbh10CXIV9kWVcje4O02_" + userId)
                            .collection("messages")
                            .whereEqualTo("isRead", false)
                            .whereEqualTo("receiver", "BzGePvzWsbh10CXIV9kWVcje4O02") // Asegura que solo obtienes mensajes no leídos dirigidos al admin.
                            .get()
                            .addOnCompleteListener(msgTask -> {
                                if (msgTask.isSuccessful()) {
                                    // Actualiza la cantidad de mensajes no leídos
                                    int unreadCount = msgTask.getResult().size();
                                    user.setUnreadMessages(unreadCount);

                                    // Actualiza la lista de usuarios
                                    if (!userList.contains(user)) {
                                        userList.add(user);
                                    } else {
                                        int index = userList.indexOf(user);
                                        userList.set(index, user);
                                    }

                                    // Notificar al adaptador que se ha actualizado un usuario
                                    userAdapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(NotiUsuario.this, "Error loading messages for " + name, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(NotiUsuario.this, "Error loading users", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
