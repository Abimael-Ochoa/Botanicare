package com.example.projectintegration.Papelera;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.projectintegration.R;

import java.util.Calendar;

public class FragmentCalendario extends Fragment {
    private Button btn_establecer_fecha, btn_guardar_recordatorio;
    private TextView tv_fecha;
    private EditText et_mensaje_personalizado;
    private RadioGroup rgAccion;
    private RadioButton rbRegar, rbFertilizar;
    private int year, month, day;

    private static final String CHANNEL_ID = "My notification channel";
    private static final int PERMISSION_REQUEST_CODE = 1;

    private String task = "regar"; // Tarea predeterminada: "regar"
    private String plantName = "aguacate"; // Nombre de la planta
    private String customMessage = ""; // Mensaje personalizado de la planta

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        btn_establecer_fecha = view.findViewById(R.id.btn_establecer_fecha);
        btn_guardar_recordatorio = view.findViewById(R.id.btn_guardar_recordatorio);
        tv_fecha = view.findViewById(R.id.tv_fecha);
        et_mensaje_personalizado = view.findViewById(R.id.et_mensaje_personalizado);
        rgAccion = view.findViewById(R.id.rg_accion);  // RadioGroup para seleccionar la acción
        rbRegar = view.findViewById(R.id.rb_regar);  // RadioButton para regar
        rbFertilizar = view.findViewById(R.id.rb_fertilizar);  // RadioButton para fertilizar

        // Configurar botón de retroceso
        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed(); // Llamar a la acción de retroceso
            }
        });

        // Establecer fecha
        btn_establecer_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Mostrar la fecha seleccionada
                                tv_fecha.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        // Guardar recordatorio
        btn_guardar_recordatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customMessage = et_mensaje_personalizado.getText().toString(); // Obtener el mensaje personalizado

                // Actualizar la tarea según la opción seleccionada
                int selectedId = rgAccion.getCheckedRadioButtonId();
                if (selectedId == rbRegar.getId()) {
                    task = "regar";
                } else if (selectedId == rbFertilizar.getId()) {
                    task = "fertilizar";
                }

                if (customMessage.isEmpty()) {
                    Toast.makeText(getContext(), "Por favor, ingresa un mensaje personalizado", Toast.LENGTH_SHORT).show();
                } else {
                    // Crear y mostrar la notificación con el mensaje personalizado
                    sendNotification();
                }
            }
        });

        // Verificar permisos para notificaciones en Android 13 (API 33) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }

        // Crear el canal de notificación si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel description");

            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        return view;
    }

    private void sendNotification() {
        // Crear el mensaje personalizado dependiendo de la tarea (regar o fertilizar)
        String actionMessage = "Es hora de " + task + " la planta.";
        String personalizedMessage = customMessage; // Incluir mensaje personalizado

        // Crear la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setContentTitle(actionMessage)  // Título personalizado: "Es hora de regar/ferilizar"
                .setContentText(personalizedMessage)  // Mensaje personalizado
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true);

        // Usar NotificationManagerCompat para enviar la notificación
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getContext());
        managerCompat.notify(1, builder.build());

        // Mostrar un mensaje de confirmación
        Toast.makeText(getContext(), "Notificación enviada", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permiso concedido para notificaciones", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permiso denegado para notificaciones", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
