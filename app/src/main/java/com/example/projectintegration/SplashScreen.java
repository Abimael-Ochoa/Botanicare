package com.example.projectintegration; // Paquete donde se encuentra la clase

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectintegration.inicio_sesion.LoginScreen;

// Clase que representa la pantalla de inicio (Splash Screen) de la aplicación
public class SplashScreen extends AppCompatActivity {
    // Declara una variable para el hilo que se usará para el temporizador
    Thread timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Llama al método onCreate de la clase padre

        // Habilita el diseño de borde a borde para la actividad
        EdgeToEdge.enable(this);

        // Establece el diseño de la actividad a activity_splash_screen
        setContentView(R.layout.activity_splash_screen);

        // Crea un nuevo hilo para manejar el temporizador
        timer = new Thread() {
            @Override
            public void run() {
                try {
                    // Utiliza synchronized para esperar durante 10 segundos
                    synchronized (this) {
                        wait(2000); // Espera 10,000 milisegundos (10 segundos)
                    }
                } catch (InterruptedException e) {
                    // Maneja la excepción en caso de que el hilo sea interrumpido
                    e.printStackTrace();
                } finally {
                    // Código que se ejecuta después de la espera
                    // Crea un nuevo Intent para iniciar la actividad MainActivity
                    Intent intent = new Intent(SplashScreen.this, LoginScreen.class);
                    startActivity(intent); // Inicia la nueva actividad
                    finish(); // Cierra la actividad actual para que no se pueda volver a ella
                }
            }
        };

        // Inicia el hilo del temporizador
        timer.start();
    }
}
