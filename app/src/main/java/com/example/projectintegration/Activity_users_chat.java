package com.example.projectintegration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Activity_users_chat extends AppCompatActivity {

    private LinearLayout userListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_chat);

        // Para manejar el ajuste de la UI para dispositivos con barras del sistema (como el notch)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa el contenedor de usuarios
        userListContainer = findViewById(R.id.user_list_container);

        // Llama a la función para cargar usuarios desde Firestore
        loadUsersFromFirestore();
    }

    // Función para cargar usuarios desde Firestore
    private void loadUsersFromFirestore() {
        // Obtiene la instancia de Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Referencia a la colección "users" de Firestore
        db.collection("users") // Cambia "users" por el nombre de tu colección en Firestore
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userListContainer.removeAllViews(); // Limpia la lista antes de cargar los nuevos usuarios

                        // Itera sobre los documentos recuperados de Firestore
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obtiene el nombre del usuario y la cantidad de mensajes no leídos
                            String userName = document.getString("name");


                            // Verifica si los valores existen y maneja el caso de valores nulos
                            if (userName != null) {

                            }
                        }
                    } else {
                        // Manejo de errores en caso de fallo en la consulta
                        // Aquí puedes mostrar un mensaje de error o hacer algún logging
                        // Toast.makeText(Activity_users_chat.this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Función para agregar un usuario a la vista
    private void addUserToList(String userName, int unreadMessages) {
        // Infla el layout del usuario
        View userView = LayoutInflater.from(this).inflate(R.layout.item_usuario, userListContainer, false);

        // Configura los datos del usuario
        ImageView userIcon = userView.findViewById(R.id.usuario_icon); // Puedes personalizar esto si tienes íconos de usuario
        TextView userNameView = userView.findViewById(R.id.tv_user_name);
        TextView notificationCount = userView.findViewById(R.id.tv_notification_count);

        // Establece el nombre del usuario en el TextView
        userNameView.setText(userName);

        // Si hay mensajes no leídos, muestra el número de mensajes
        if (unreadMessages > 0) {
            notificationCount.setText(String.valueOf(unreadMessages));
            notificationCount.setVisibility(View.VISIBLE); // Muestra el contador de notificaciones
        } else {
            notificationCount.setVisibility(View.GONE); // Oculta el contador de notificaciones si no hay
        }

        // Agrega la vista del usuario al contenedor
        userListContainer.addView(userView);
    }
}
