package com.example.projectintegration;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Fragment_NotiUsuario extends Fragment {

    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    private ArrayList<UserChat> userList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__noti_usuario, container, false);

        // Configurar botón de retroceso
        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed(); // Llamar a la acción de retroceso
            }
        });

        rvUsers = view.findViewById(R.id.rv_users);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        rvUsers.setAdapter(userAdapter);



        loadUsersFromFirebase();

        return view;
    }


    private void openChatFragment(UserChat user) {
        Fragment_Mensajes chatFragment = new Fragment_Mensajes();

        // Pasar datos al fragmento de chat
        Bundle bundle = new Bundle();
        bundle.putString("userName", user.getName());
        chatFragment.setArguments(bundle);

        // Reemplazar el fragmento
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_mensajes, chatFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadUsersFromFirebase() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    // Obtener los datos del usuario
                    String name = userSnapshot.child("name").getValue(String.class);
                    Long unreadMessages = userSnapshot.child("unreadMessages").getValue(Long.class);

                    // Validar datos no nulos y agregar a la lista
                    if (name != null && unreadMessages != null) {
                        userList.add(new UserChat(name, unreadMessages.intValue()));
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading users", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
