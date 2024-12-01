package com.example.projectintegration.catalogo_plantas;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.projectintegration.R;

public class CatalogoAdmin extends AppCompatActivity {

    private Toolbar toolbar;

    private PantallaCatalogo pantallaCatalogo;

    private CatalogoAdmin(PantallaCatalogo pantallaCatalogo) {
        this.pantallaCatalogo = pantallaCatalogo;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_catalogo_admin);

    }
    @Override
    public void onBackPressed() {
        // Si estamos en FragmentCalendario, mostramos el Toolbar al regresar
        if (toolbar.getVisibility() == View.GONE) {
            toolbar.setVisibility(View.VISIBLE); // Mostrar el Toolbar
        }

        // Si hay un fragmento en la pila de retroceso, lo eliminamos
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(); // Elimina el fragmento actual
        } else {
            super.onBackPressed(); // Volver al Activity principal
        }
    }


}