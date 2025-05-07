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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = firebaseUser.getEmail();

        if (searchInput == null) {
            Context context = toolbar.getContext();

            // Crear y configurar TextInputLayout
            searchBar = new TextInputLayout(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(16, 8, 16, 8);
            searchBar.setLayoutParams(layoutParams);
            searchBar.setHint("Buscar...");
            searchBar.setHintTextColor(ColorStateList.valueOf(
                    context.getResources().getColor(R.color.white)));
            searchBar.setBoxBackgroundColor(
                    context.getResources().getColor(R.color.white));
            searchBar.setBoxStrokeColor(
                    context.getResources().getColor(R.color.white));

            // Crear y configurar TextInputEditText
            searchInput = new TextInputEditText(context);
            searchInput.setInputType(InputType.TYPE_CLASS_TEXT);
            searchInput.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            searchInput.setTextColor(context.getResources().getColor(R.color.white));
            searchInput.setHintTextColor(context.getResources().getColor(R.color.white));
            searchInput.setTextSize(16);
            searchInput.setPadding(16, 16, 16, 16);

            // Añadir TextInputEditText al TextInputLayout
            searchBar.addView(searchInput);

            // Añadir TextInputLayout al Toolbar
            toolbar.addView(searchBar);

            // Ocultar otros elementos del Toolbar
            toolBarTitle.setVisibility(View.GONE);
            addButton.setVisibility(View.GONE);

            // Detectar cambios en el texto y lanzar la búsqueda
            searchInput.addTextChangedListener(new android.text.TextWatcher() {
                @Override public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (onSearchListener != null) {
                        onSearchListener.onSearch(s.toString().trim());
                    }
                }
                @Override public void afterTextChanged(Editable editable) {}
            });

        } else {
            // 1) Limpiar el texto para la próxima apertura
            searchInput.setText("");

            // 2) Restaurar la UI original
            toolbar.removeView(searchBar);
            searchBar = null;
            searchInput = null;
            toolBarTitle.setVisibility(View.VISIBLE);
            if ("admin@admin.com".equals(email)) {
                addButton.setVisibility(View.VISIBLE);
            } else {
                addButton.setVisibility(View.GONE);
            }

            // 3) Lanzar búsqueda vacía para recargar todo el catálogo
            if (onSearchListener != null) {
                onSearchListener.onSearch("");
            }
        }
    }



}
