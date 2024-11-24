package com.example.projectintegration.utilities;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.projectintegration.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SearchBarCatalogo {

    private final Toolbar toolbar;
    private final ImageView searchButton;
    private final ImageView addButton;
    private final TextView toolBarTitle;
    private TextInputLayout searchBar;
    private TextInputEditText searchInput;

    public SearchBarCatalogo(Toolbar toolbar, ImageView searchButton, ImageView addButton, TextView toolBarTitle) {
        this.toolbar = toolbar;
        this.searchButton = searchButton;
        this.addButton = addButton;
        this.toolBarTitle = toolBarTitle;

        setupSearchButtonListener();
    }

    private void setupSearchButtonListener() {
        searchButton.setOnClickListener(v -> toggleSearchInput());
    }

    private OnSearchListener onSearchListener;

    public void setOnSearchListener(OnSearchListener listener) {
        this.onSearchListener = listener;
    }

    public interface OnSearchListener {
        void onSearch(String query);
    }

    private void toggleSearchInput() {
        if (searchInput == null) {
            // Crear el TextInput dinámicamente
            Context context = toolbar.getContext();
            searchBar = new TextInputLayout(context);

            // Usar LinearLayout.LayoutParams en lugar de Toolbar.LayoutParams
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            searchBar.setLayoutParams(layoutParams);
            searchBar.setHint("Buscar...");
            searchBar.setHintTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.black)));
            searchBar.setBackgroundColor(context.getResources().getColor(R.color.white));  // Color de fondo

            searchInput = new TextInputEditText(context);
            searchInput.setInputType(InputType.TYPE_CLASS_TEXT);
            searchInput.setLayoutParams(layoutParams);  // Usar los mismos LayoutParams para el TextInputEditText
            searchInput.setTextColor(context.getResources().getColor(R.color.black));  // Color del texto ingresado
            searchBar.addView(searchInput);

            // Añadir la barra de búsqueda al Toolbar
            toolbar.addView(searchBar);

            // Ocultar el título y el botón de agregar
            toolBarTitle.setVisibility(View.GONE);
            addButton.setVisibility(View.GONE);
        } else {
            // Si ya existe, realiza la búsqueda
            String query = searchInput.getText().toString().trim();
            if (!query.isEmpty() && onSearchListener != null) {
                onSearchListener.onSearch(query);  // Ejecuta la búsqueda
            }

            // Restaurar el estado original
            toolbar.removeView(searchBar);
            searchInput = null;
            toolBarTitle.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
        }
    }



}
