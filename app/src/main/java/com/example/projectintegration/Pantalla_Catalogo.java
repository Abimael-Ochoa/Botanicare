package com.example.projectintegration;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.navigation.NavigationView;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

public class Pantalla_Catalogo extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    ImageView logOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_catalogo);


        // Configuración del Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

// Inicializa el ImageView como botón de logout
        logOutButton = findViewById(R.id.searchButton); // Asegúrate de que el ID coincide con el de tu XML

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear el Intent para abrir otra actividad (página)
                Intent intent = new Intent(Pantalla_Catalogo.this, EdicionPlantaActivity.class);

                // Iniciar la actividad
                startActivity(intent);
            }
        });

        // Oculta el título predeterminado
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Configura el TextView como título
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Botanicare");  // Título centrado

        // Configuración del DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);

        // Eliminar ActionBarDrawerToggle por completo, no lo usaremos
        // Ajusta el tamaño del ícono de hamburguesa y usa tu ícono personalizado
        Drawable menuIcon = getResources().getDrawable(R.drawable.menu64); // Cambia "menu64" por el nombre de tu ícono
        if (menuIcon != null) {
            // Cambia el tamaño del ícono
            menuIcon.setBounds(0, 0, 120, 120); // Ajusta el tamaño (ancho, alto) como lo necesites
            toolbar.setNavigationIcon(menuIcon);  // Vuelve a establecer el ícono modificado
        }

        // Configura la acción para el nuevo ícono
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);  // Cierra el Drawer
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);  // Abre el Drawer
                }
            }
        });

        // Configuración del NavigationView
        NavigationView navView = findViewById(R.id.nav_view);
        if (navView != null) {
            navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    Fragment fragment = null;

                    // Aquí decides cuál fragmento mostrar
                    if (id == R.id.nav_calendario) {
                        fragment = new FragmentCalendario(); // Fragmento de calendario
                    }

                    // Si el fragmento es válido
                    if (fragment != null) {
                        // Mostrar u ocultar el toolbar dependiendo del fragmento
                        if (id == R.id.nav_calendario) {
                            toolbar.setVisibility(View.GONE); // Oculta el Toolbar
                        } else {
                            toolbar.setVisibility(View.VISIBLE); // Muestra el Toolbar
                        }

                        // Reemplazar el contenido del frame con el fragmento seleccionado
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content_frame, fragment); // Reemplaza solo el contenido en content_frame
                        transaction.addToBackStack(null);  // Agregar la transacción al back stack para poder regresar
                        transaction.commit();
                    }

                    // Cerrar el drawer después de seleccionar
                    if (drawerLayout != null) {
                        drawerLayout.closeDrawers();
                    }
                    return true;
                }
            });

            // Cambiar la tipografía de los elementos del menú
            Menu menu = navView.getMenu();
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                SpannableString spanString = new SpannableString(item.getTitle());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    spanString.setSpan(new CustomTypefaceSpan("my_font", getResources().getFont(R.font.my_custom_font)), 0, spanString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                item.setTitle(spanString);
            }
        }

        // Cargar el fragmento por defecto (si es necesario)
        if (savedInstanceState == null) {
            // Si la app se inicia por primera vez, carga el fragmento por defecto
            Fragment defaultFragment = new Fragment_Content(); // Asegúrate de que el fragmento por defecto sea cargado
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, defaultFragment)
                    .commit();
        }


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

    // Clase CustomTypefaceSpan
    // Clase CustomTypefaceSpan
    public class CustomTypefaceSpan extends TypefaceSpan {

        private final Typeface newType;

        public CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }

        @Override
        public void updateDrawState(TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }

        // Eliminamos la palabra clave 'static' de aquí
        private void applyCustomTypeFace(TextPaint paint, Typeface tf) {
            int oldStyle = paint.getTypeface() != null ? paint.getTypeface().getStyle() : 0;
            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }
            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }
            paint.setTypeface(tf);
        }
    }

}
