package com.example.projectintegration;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
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

import com.example.projectintegration.inicio_sesion.LoginScreen;
import com.example.projectintegration.utilities.ErrorHandler;

import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class FragmentRecordatorio extends Fragment {
    private ImageButton btn_establecer_fecha;
   private Button btn_guardar_recordatorio;
    private TextView tv_fecha;
    private EditText et_mensaje_personalizado;
    private RadioGroup rgAccion;
    private RadioButton rbRegar, rbFertilizar;
    private int year, month, day;
    TextView errorMessage;

    private ImageButton btn_establecer_hora; // Botón para seleccionar hora
    private TextView tv_hora; // TextView para mostrar la hora seleccionada
    private int hour, minute; // Variables para hora y minuto


    private static final String CHANNEL_ID = "My notification channel";
    private static final int PERMISSION_REQUEST_CODE = 1;

    private String task = "regar"; // Tarea predeterminada: "regar"
    private String plantName = "aguacate"; // Nombre de la planta
    private String customMessage = ""; // Mensaje personalizado de la planta

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rercordatorio, container, false);
        errorMessage = view.findViewById(R.id.errorMessage);
        btn_establecer_fecha = view.findViewById(R.id.bt_fecha);
        btn_guardar_recordatorio = view.findViewById(R.id.btn_guardar_recordatorio);
        tv_fecha = view.findViewById(R.id.tv_fecha);
        et_mensaje_personalizado = view.findViewById(R.id.et_mensaje_personalizado);
        rgAccion = view.findViewById(R.id.rg_accion);  // RadioGroup para seleccionar la acción
        rbRegar = view.findViewById(R.id.rb_regar);  // RadioButton para regar
        rbFertilizar = view.findViewById(R.id.rb_fertilizar);  // RadioButton para fertilizar

        btn_establecer_hora = view.findViewById(R.id.bt_hora);
        tv_hora = view.findViewById(R.id.tv_hora);

        // Configurar cambio dinámico del fondo de los RadioButtons
        rgAccion.setOnCheckedChangeListener((group, checkedId) -> {
            // Actualiza los fondos según la selección
            if (checkedId == rbRegar.getId()) {
                rbRegar.setBackgroundResource(R.drawable.radio_button_border); // Fondo seleccionado
                rbFertilizar.setBackgroundResource(R.drawable.bc_radiobutton); // Fondo no seleccionado
            } else if (checkedId == rbFertilizar.getId()) {
                rbFertilizar.setBackgroundResource(R.drawable.radio_button_border); // Fondo seleccionado
                rbRegar.setBackgroundResource(R.drawable.bc_radiobutton); // Fondo no seleccionado
            }
        });

        btn_establecer_hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la hora actual
                Calendar calendar = Calendar.getInstance();
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);

                // Mostrar el TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int selectedMinute) {
                                // Actualiza el TextView con la hora seleccionada
                                String formattedTime = String.format("%02d:%02d", hourOfDay, selectedMinute);
                                tv_hora.setText(formattedTime);
                            }
                        }, hour, minute, true); // true para formato 24 horas
                timePickerDialog.show();
            }
        });


        et_mensaje_personalizado.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Limpia el estilo de error cuando el usuario comienza a escribir
                ErrorHandler.resetFieldStyles(getContext(),et_mensaje_personalizado);
                errorMessage.setVisibility(View.GONE); // Oculta el mensaje de error
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


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

// Dentro de btn_guardar_recordatorio.setOnClickListener
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
                    ErrorHandler.setFieldErrorStyle(et_mensaje_personalizado, getContext());
                    errorMessage.setText("Por favor ingresa un mensaje");
                    errorMessage.setVisibility(View.VISIBLE);
                } else {
                    // Verifica permisos antes de programar la alarma
                    checkExactAlarmPermissionAndProceed();
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


    // Método para verificar el permiso y proceder
    private void checkExactAlarmPermissionAndProceed() {
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12 o superior
            if (!alarmManager.canScheduleExactAlarms()) {
                // Solicitar al usuario que permita las alarmas exactas
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                Toast.makeText(getContext(), "Concede permiso para programar alarmas exactas", Toast.LENGTH_LONG).show();
            } else {
                // Si el permiso está concedido, programa la notificación
                sendNotification();
            }
        } else {
            // Para versiones anteriores a Android 12, no es necesario el permiso
            sendNotification();
        }
    }

// Otros imports

    @SuppressLint("ScheduleExactAlarm")
    private void sendNotification() {
        String actionMessage = "Es hora de " + task + " la planta.";
        String personalizedMessage = customMessage;

        String fecha = tv_fecha.getText().toString();
        String hora = tv_hora.getText().toString();

        if (fecha.equals("Fecha: -") || hora.equals("Hora: -")) {
            Toast.makeText(getContext(), "Selecciona fecha y hora", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parsear la fecha y hora seleccionadas
        String[] dateParts = fecha.split("/");
        String[] timeParts = hora.split(":");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(dateParts[2]));
        calendar.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1); // Mes base 0
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[0]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
        calendar.set(Calendar.SECOND, 0);

        // Configurar el intent para el BroadcastReceiver
        Intent intent = new Intent(getContext(), NotificationReceiver.class);
        intent.putExtra("notificationTitle", actionMessage);
        intent.putExtra("notificationMessage", personalizedMessage);

        // Crear el PendingIntent para el AlarmManager
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Configurar AlarmManager
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        Toast.makeText(getContext(), "Recordatorio programado", Toast.LENGTH_SHORT).show();
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
