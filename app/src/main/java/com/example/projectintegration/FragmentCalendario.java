package com.example.projectintegration;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class FragmentCalendario extends Fragment {
    Button btn_noti;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        btn_noti = view.findViewById(R.id.btn_notificacion);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }


        btn_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Código de la notificación
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "My notification")
                        .setContentTitle("My title")
                        .setContentText("Notificación de prueba")
                        .setSmallIcon(R.drawable.ic_notification)
                        .setAutoCancel(true);

                // Usar NotificationManagerCompat para enviar la notificación
                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getContext());
                managerCompat.notify(1, builder.build());

                // Opcional: muestra un mensaje en la pantalla como confirmación
                Toast.makeText(getContext(), "Notificación enviada", Toast.LENGTH_SHORT).show();
            }
        });

        // Obtén el ImageView y verifica que no sea nulo
        ImageView backButton = view.findViewById(R.id.regreso_button);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Llamar a onBackPressed para regresar a la MainActivity
                    getActivity().onBackPressed();  // Llama al método onBackPressed() de la actividad
                }
            });
        }

        return view;
    }
}
