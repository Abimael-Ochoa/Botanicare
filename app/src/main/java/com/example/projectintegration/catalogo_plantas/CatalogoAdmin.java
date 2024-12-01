package com.example.projectintegration.catalogo_plantas;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectintegration.R;

public class CatalogoAdmin extends AppCompatActivity {

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
}