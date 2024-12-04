package com.example.projectintegration.utilities;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
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
            Context context = toolbar.getContext();

            // Crear y configurar TextInputLayout
            searchBar = new TextInputLayout(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(16, 8, 16, 8); // Agregar márgenes
            searchBar.setLayoutParams(layoutParams);
            searchBar.setHint("Buscar...");
            searchBar.setHintTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
            searchBar.setBoxBackgroundColor(context.getResources().getColor(R.color.white)); // Fondo de la barra
            searchBar.setBoxStrokeColor(context.getResources().getColor(R.color.white)); // Color del borde

            // Crear y configurar TextInputEditText
            searchInput = new TextInputEditText(context);
            searchInput.setInputType(InputType.TYPE_CLASS_TEXT);
            searchInput.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            searchInput.setTextColor(context.getResources().getColor(R.color.white)); // Color del texto
            searchInput.setHintTextColor(context.getResources().getColor(R.color.white)); // Color del hint
            searchInput.setTextSize(16); // Tamaño del texto
            searchInput.setPadding(16, 16, 16, 16); // Espaciado interno

            // Añadir TextInputEditText al TextInputLayout
            searchBar.addView(searchInput);

            // Añadir TextInputLayout al Toolbar
            toolbar.addView(searchBar);

            // Ocultar otros elementos del Toolbar
            toolBarTitle.setVisibility(View.GONE);
            addButton.setVisibility(View.GONE);

            // Configurar listener para detectar cambios en el texto
            searchInput.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (onSearchListener != null) {
                        onSearchListener.onSearch(s.toString().trim());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

        } else {
            // Restaurar el estado original
            toolbar.removeView(searchBar);
            searchInput = null;
            toolBarTitle.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
        }
    }


}
