package com.example.projectintegration.catalogo_plantas;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.projectintegration.FragmentMisPlantas;
import com.example.projectintegration.ProgressPlant;
import com.example.projectintegration.chat_codigo.ChatUser;
import com.example.projectintegration.EdicionPlantaActivity;
import com.example.projectintegration.FragmentPlantProgress;
import com.example.projectintegration.FragmentRecordatorio;
import com.example.projectintegration.Fragment_Consejos;
import com.example.projectintegration.Fragment_Content;
import com.example.projectintegration.NotiUsuario;
import com.example.projectintegration.PlantasAdquiridasUsuario;
import com.example.projectintegration.inicio_sesion.LoginScreen;
import com.example.projectintegration.R;
import com.example.projectintegration.models.User;
import com.example.projectintegration.registro_pedido_plantas.RegistroPedidoAdminFragment;
import com.example.projectintegration.registro_pedido_plantas.RegistroPedidoUser;
import com.example.projectintegration.utilities.SearchBarCatalogo;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.widget.Toast;

public class PantallaCatalogo extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    ImageView logOutButton;
    private Toolbar toolbar;
    ImageView addButton;
    TextView toolBarTitle;
    ImageView searchButton;
    private SearchBarCatalogo searchBarCatalogo;
    private FirebaseUser firebaseUser;
    User usuarioActual;
    private boolean isAdmin = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_catalogo);

        usuarioActual = new User();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            // El usuario está autenticado

            String email = firebaseUser.getEmail(); // Obtener el correo electrónico del usuario

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference usersRef = db.collection("users");

            usersRef.whereEqualTo("email", email).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                // Se encontró un documento que coincide con el correo electrónico
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0); // Asumimos que solo hay un documento con ese emailusuarioActual = document.toObject(User.class); // Mapeamos los datos a un objeto User
                                if (firebaseUser != null) {
                                    if ("admin@admin.com".equalsIgnoreCase(firebaseUser.getEmail())) {
                                        isAdmin = true;
                                    }
                                    // Ahora puedes usar el objeto `user` con los datos obtenidos de Firestore
                                    Log.d("User Data", "Name: " + usuarioActual.getName() + ", Phone: " + usuarioActual.getPhone());
                                } else {
                                    // No se encontró un usuario con ese email en Firestore
                                    Log.d("Firestore", "No such user with this email");
                                }
                            } else {
                                // Error al realizar la consulta
                                Log.d("Firestore", "Error getting documents: ", task.getException());
                            }
                        }
                    });

        } else {
            // El usuario no está autenticado
            // Maneja el caso donde no hay usuario autenticado
        }





        // Cargar el fragmento por defecto (si es necesario)
        if (savedInstanceState == null) {
            // Si la app se inicia por primera vez, carga el fragmento por defecto
            Fragment defaultFragment = new Fragment_Content(); // Asegúrate de que el fragmento por defecto sea cargado
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame,  new CargarPlantasCatalogo())
                    .commit();
        }
        toolbarLogic();
        drawerLayoutConfig();
        navViewConfig();
    }


    private void navViewConfig( ) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = firebaseUser.getEmail(); // Obtener el correo electrónico del usuario
        // Configuración del NavigationView
        NavigationView navView = findViewById(R.id.nav_view);
        if (navView != null) {
            Menu navMenu = navView.getMenu();
            if ("admin@admin.com".equalsIgnoreCase(email)) {
                navMenu.findItem(R.id.nav_misplantas).setVisible(false);
            }
            navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    Fragment fragment = null;

                    // Aquí decides cuál fragmento mostrar
                    if (id == R.id.nav_calendario) {
                        fragment = new FragmentRecordatorio(); // Fragmento de calendario
                    }else if (id == R.id.nav_chat) {

                        if ("admin@admin.com".equalsIgnoreCase(email)) {
                            Intent intent = new Intent(PantallaCatalogo.this, NotiUsuario.class);
                            intent.putExtra("userName", "demo");
                            intent.putExtra("unreadMessages", 0);
                            intent.putExtra("userId", usuarioActual.getId()); // UID del usuario receptor
                            startActivity(intent);
                            finish(); // Finalizar la actividad actual
                        }else{
                            Intent intent = new Intent(PantallaCatalogo.this, ChatUser.class);
                            intent.putExtra("userName", "demo");
                            intent.putExtra("unreadMessages", 0);
                            intent.putExtra("userId", usuarioActual.getId()); // UID del usuario receptor
                            startActivity(intent);
                            finish(); // Finalizar la actividad actual
                        }

                    }else if (id == R.id.nav_registrarP){
                        if ("admin@admin.com".equalsIgnoreCase(email)) {
                            fragment = new RegistroPedidoAdminFragment();
                        }else{
                            fragment = new RegistroPedidoUser();
                        }
                    } else if (id == R.id.nav_progreso) {

                        if ("admin@admin.com".equalsIgnoreCase(email)) {
                            Intent intent = new Intent(PantallaCatalogo.this, ProgressPlant.class);
                            startActivity(intent);
                            finish(); // Finalizar la actividad actual
                        }else{
                            fragment = new FragmentPlantProgress();
                        }

                    } else if (id == R.id.nav_consejos) {
                        fragment = new Fragment_Consejos();
                    }else if (id == R.id.nav_plantas_adquiridas) {
                        fragment = new RegistroPedidoUser();
                    }else if (id == R.id.nav_misplantas) {
                        fragment = new FragmentMisPlantas();
                    }



                    // Si el fragmento es válido
                    if (fragment != null) {
                        // Mostrar u ocultar el toolbar dependiendo del fragmento
                        if (id == R.id.nav_calendario) {
                            toolbar.setVisibility(View.GONE); // Oculta el Toolbar
                        } else if (id == R.id.nav_chat){
                            toolbar.setVisibility(View.GONE); // Oculta el Toolbar
                        } else if (id == R.id.nav_registrarP) {
                            toolbar.setVisibility(View.GONE);
                        } else if (id == R.id.nav_progreso) {
                            toolbar.setVisibility(View.GONE);
                        } else if (id == R.id.nav_consejos) {
                            toolbar.setVisibility(View.GONE);

                        } else if (id == R.id.nav_plantas_adquiridas) {
                            toolbar.setVisibility(View.GONE);
                        }
                    } else if (item.getItemId() == R.id.nav_logout) {

                        // Mostrar el dialogo de confirmación antes de cerrar sesión
                        // Inflar el layout personalizado
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.custom_alert, null);



                        // Crear el AlertDialog con el tema personalizado
                        AlertDialog.Builder builder = new AlertDialog.Builder(PantallaCatalogo.this, R.style.TransparentDialogTheme);

                        builder.setView(dialogView);

                        // Obtener los botones y otros elementos del layout
                        Button btnConfirmar = dialogView.findViewById(R.id.btnconfirmar);
                        Button btnCancelar = dialogView.findViewById(R.id.btncancelar);

                        // Crear el dialogo
                        AlertDialog alertDialog = builder.create();

                        // Configurar el botón "Cerrar Sesión"
                        btnConfirmar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Cerrar sesión en Firebase
                                mAuth.signOut();

                                // Redirigir a la actividad de login
                                Intent intent = new Intent(PantallaCatalogo.this, LoginScreen.class);
                                startActivity(intent);
                                finish(); // Finalizar la actividad actual
                                alertDialog.dismiss(); // Cerrar el dialogo
                            }
                        });

                        // Configurar el botón "Cancelar"
                        btnCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss(); // Solo cerrar el dialogo, no hacer nada más
                            }
                        });

                        // Mostrar el dialogo
                        alertDialog.show();
                    }
                    else {
                        toolbar.setVisibility(View.VISIBLE); // Muestra el Toolbar
                    }

                    if(fragment!= null){
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
    }

    private void drawerLayoutConfig() {
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
    }

    private void toolbarLogic() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = firebaseUser.getEmail(); // Obtener el correo electrónico del usuario

        toolbar = findViewById(R.id.toolbar);
        // Inicializa el ImageView como botón de logout
        addButton = findViewById(R.id.addButton);
        toolBarTitle = findViewById(R.id.toolbar_title);
        // Configuración del DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        searchButton = findViewById(R.id.searchButton);

        if (firebaseUser != null) {
            if (!"admin@admin.com".equalsIgnoreCase(email)) {
                addButton.setVisibility(View.GONE);
            }
        }

        mAuth = FirebaseAuth.getInstance();

        // Cambiar el color de la barra de estado (si la versión es Lollipop o superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.tu_color_verde)); // Reemplaza 'tu_color_verde' con el color que desees
        }

        // Configuración del Toolbar
        setSupportActionBar(toolbar);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear el Intent para abrir otra actividad (página)
                Intent intent = new Intent(PantallaCatalogo.this, EdicionPlantaActivity.class);

                // Iniciar la actividad
                startActivity(intent);
            }
        });
        // Configura la barra de búsqueda
        searchBarCatalogo = new SearchBarCatalogo(toolbar, searchButton, addButton, toolBarTitle);

        // Escucha la barra de búsqueda para realizar búsquedas
        searchBarCatalogo.setOnSearchListener(query -> {

            filterPlants(query);
        });
// Inicializa el ImageView como botón de logout
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear el Intent para abrir otra actividad (página)
                Intent intent = new Intent(PantallaCatalogo.this, EdicionPlantaActivity.class);

                // Iniciar la actividad
                startActivity(intent);
            }
        });

        // Oculta el título predeterminado
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Configura el TextView como título
        toolBarTitle.setText("Botanicare");  // Título centrado
    }


    private void filterPlants(String query) {
        // Buscar en el fragmento PlantsGridFragment
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (fragment instanceof CargarPlantasCatalogo) {
            CargarPlantasCatalogo plantsGridFragment = (CargarPlantasCatalogo) fragment;
            plantsGridFragment.filterPlants(query); // Filtrar las plantas desde el fragmento
        } else {
            // Si no se encuentra el fragmento, puedes realizar la búsqueda de otra manera
            Toast.makeText(this, "Fragmento no encontrado", Toast.LENGTH_SHORT).show();
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
