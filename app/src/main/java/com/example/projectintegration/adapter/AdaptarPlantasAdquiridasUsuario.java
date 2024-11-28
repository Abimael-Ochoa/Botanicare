package com.example.projectintegration.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.projectintegration.R;
import com.example.projectintegration.models.PAdquridasUsuario;

import java.util.List;

public class AdaptarPlantasAdquiridasUsuario extends BaseAdapter{
    private Context context;
    private List<PAdquridasUsuario> plantas;

    // Constructor
    public AdaptarPlantasAdquiridasUsuario(Context context, List<PAdquridasUsuario> plantas) {
        this.context = context;
        this.plantas = plantas;
    }

    @Override
    public int getCount() {
        return plantas.size();
    }

    @Override
    public Object getItem(int position) {
        return plantas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_plantas_adquiridas, parent, false);
        }

        // Obtener los elementos del layout
        ImageView ivPlanta = convertView.findViewById(R.id.plant_image);
        TextView tvNombrePlanta = convertView.findViewById(R.id.plant_name);
        TextView tvFechaAdquirida = convertView.findViewById(R.id.tvFechaAdquirida);

        // Asignar valores de la planta actual
        PAdquridasUsuario planta = plantas.get(position);
        ivPlanta.setImageResource(planta.getImagenResId());
        tvNombrePlanta.setText(planta.getNombre());
        tvFechaAdquirida.setText("Adquirida el " + planta.getFechaAdquisicion());

        return convertView;
    }
}
