package com.example.projectintegration.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectintegration.R;
import com.example.projectintegration.models.Consejo;

import java.util.List;

public class ConsejoAdapter extends RecyclerView.Adapter<ConsejoAdapter.ConsejoViewHolder> {

    private List<Consejo> listaConsejos;

    public ConsejoAdapter(List<Consejo> listaConsejos) {
        this.listaConsejos = listaConsejos;
    }

    @NonNull
    @Override
    public ConsejoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_consejo, parent, false);
        return new ConsejoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsejoViewHolder holder, int position) {
        Consejo consejo = listaConsejos.get(position);
        holder.tvNombreUsuario.setText(consejo.getNombreUsuario());
        holder.tvTextoConsejo.setText(consejo.getTextoConsejo());
        holder.ivFotoPerfil.setImageResource(consejo.getFotoPerfil());
    }

    @Override
    public int getItemCount() {
        return listaConsejos.size();
    }

    static class ConsejoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreUsuario, tvTextoConsejo;
        ImageView ivFotoPerfil;

        public ConsejoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreUsuario = itemView.findViewById(R.id.tvNombreUsuario);
            tvTextoConsejo = itemView.findViewById(R.id.tvTextoConsejo);
            ivFotoPerfil = itemView.findViewById(R.id.ivFotoPerfil);
        }
    }
}

