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
import com.example.projectintegration.chat_codigo.Chat;
import com.example.projectintegration.inicio_sesion.LoginScreen;
import com.example.projectintegration.models.User;
import com.example.projectintegration.utilities.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

        Utils.changeStatusBarColor(this, R.color.tu_color_verde);

        // Inicializar FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Referencia a la colección "users" en Firestore
        usersRef = FirebaseFirestore.getInstance().collection("users");

        // Configurar botón de retroceso
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Redirigir a la actividad de login
                Intent intent = new Intent(NotiUsuario.this, PantallaCatalogo.class);
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

        //loadUsersFromFirestore();  // Cambiado para usar Firestore
    }

    private void openChatActivity(User user) {
        Intent intent = new Intent(this, Chat.class);
        intent.putExtra("userName", user.getName());
        intent.putExtra("unreadMessages", user.getUnreadMessages());
        intent.putExtra("userId", user.getId()); // UID del usuario receptor
        startActivity(intent);

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsersFromFirestore();
    }



// …

    private void loadUsersFromFirestore() {
        usersRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(this, "Error loading users", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1) Filtrar admin y preparar documentos
            List<DocumentSnapshot> docs = new ArrayList<>();
            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                if (!"BzGePvzWsbh10CXIV9kWVcje4O02".equals(doc.getId())) {
                    docs.add(doc);
                }
            }

            userList.clear();
            int total = docs.size();
            if (total == 0) {
                userAdapter.notifyDataSetChanged();
                return;
            }
            AtomicInteger processed = new AtomicInteger(0);

            // 2) Para cada usuario, consulta unread y último mensaje
            for (DocumentSnapshot userDoc : docs) {
                String userId = userDoc.getId();
                String name   = userDoc.getString("name");
                User user     = new User(name, 0, userId);

                // Genera un chatID consistente
                String chatId = makeChatId("BzGePvzWsbh10CXIV9kWVcje4O02", userId);
                CollectionReference messages = FirebaseFirestore
                        .getInstance()
                        .collection("chats")
                        .document(chatId)
                        .collection("messages");

                // i) Mensajes no leídos
                messages
                        .whereEqualTo("isRead", false)
                        .whereEqualTo("receiver", "BzGePvzWsbh10CXIV9kWVcje4O02")
                        .get()
                        .addOnSuccessListener(unreadSnap -> {
                            user.setUnreadMessages(unreadSnap.size());

                            // ii) Último mensaje
                            messages
                                    .orderBy("timestamp", Query.Direction.DESCENDING)
                                    .limit(1)
                                    .get()
                                    .addOnSuccessListener(lastSnap -> {
                                        if (!lastSnap.isEmpty()) {
                                            Long ts = lastSnap.getDocuments()
                                                    .get(0)
                                                    .getLong("timestamp");
                                            user.setLastMessageTimestamp(ts != null ? ts : 0L);
                                        }

                                        // iii) Añadir y comprobar fin de todas las consultas
                                        userList.add(user);
                                        if (processed.incrementAndGet() == total) {
                                            // Ordena por último mensaje (descendente)
                                            Collections.sort(userList, (u1, u2) ->
                                                    Long.compare(u2.getLastMessageTimestamp(),
                                                            u1.getLastMessageTimestamp()));
                                            userAdapter.notifyDataSetChanged();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Aunque falle la 2ª, se agrega para no bloquear el flujo
                                        userList.add(user);
                                        if (processed.incrementAndGet() == total) {
                                            Collections.sort(userList, (u1, u2) ->
                                                    Long.compare(u2.getLastMessageTimestamp(),
                                                            u1.getLastMessageTimestamp()));
                                            userAdapter.notifyDataSetChanged();
                                        }
                                    });
                        })
                        .addOnFailureListener(e -> {
                            // Si falla la primera query, igual contamos para no bloquear
                            userList.add(user);
                            if (processed.incrementAndGet() == total) {
                                Collections.sort(userList, (u1, u2) ->
                                        Long.compare(u2.getLastMessageTimestamp(),
                                                u1.getLastMessageTimestamp()));
                                userAdapter.notifyDataSetChanged();
                            }
                        });
            }
        });
    }

    /** Genera siempre el mismo ID de chat ordenando las UIDs */
    private String makeChatId(String uid1, String uid2) {
        return uid1.compareTo(uid2) < 0
                ? uid1 + "_" + uid2
                : uid2 + "_" + uid1;
    }


}
