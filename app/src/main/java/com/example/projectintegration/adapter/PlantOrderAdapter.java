package com.example.projectintegration.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectintegration.R;
import com.example.projectintegration.models.PlantOrder;

import java.util.List;

public class PlantOrderAdapter extends RecyclerView.Adapter<PlantOrderAdapter.PlantOrderViewHolder> {

    private List<PlantOrder> plantOrders;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int orderCode);
    }

    public PlantOrderAdapter(List<PlantOrder> plantOrders, OnItemClickListener onItemClickListener) {
        this.plantOrders = plantOrders;
        this.onItemClickListener = onItemClickListener;
    }
    public void setPlantOrders(List<PlantOrder> plantOrders) {
        this.plantOrders = plantOrders;
        notifyDataSetChanged(); // Notificar al RecyclerView que los datos han cambiado
    }

    @NonNull
    @Override
    public PlantOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new PlantOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlantOrderViewHolder holder, int position) {
        PlantOrder plantOrder = plantOrders.get(position);

        // Configuración de los textos
        holder.orderCodeTextView.setText("ID: " + plantOrder.getOrderCode());
        holder.userNameTextView.setText(plantOrder.getCliente().getName());

        // Configuración de status basado en el valor Boolean
        Boolean status = plantOrder.getStatus();
        holder.status.setText(status != null && status ? "Aceptado" : "Pendiente"); // Mostrar true/false
        holder.status.setTextColor(status != null && status ? Color.parseColor("#4CAF50") : Color.parseColor("#FF5722")); // Verde o naranja

        // Configuración del listener de clics
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(plantOrder.getOrderCode());
            }
        });
    }

    @Override
    public int getItemCount() {
        return plantOrders.size();
    }

    public static class PlantOrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderCodeTextView;
        TextView userNameTextView;
        TextView status;

        public PlantOrderViewHolder(View itemView) {
            super(itemView);
            orderCodeTextView = itemView.findViewById(R.id.order_code);
            userNameTextView = itemView.findViewById(R.id.user_order);
            status = itemView.findViewById(R.id.order_status);
        }
    }
}
