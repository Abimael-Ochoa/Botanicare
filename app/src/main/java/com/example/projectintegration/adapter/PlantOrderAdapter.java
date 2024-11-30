package com.example.projectintegration.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projectintegration.R;
import com.example.projectintegration.models.PlantOrder;

import java.util.List;

public class PlantOrderAdapter extends RecyclerView.Adapter<PlantOrderAdapter.PlantOrderViewHolder> {

    private List<PlantOrder> plantOrders;

    public PlantOrderAdapter(List<PlantOrder> plantOrders) {
        this.plantOrders = plantOrders;
    }

    @NonNull
    @Override
    public PlantOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial, parent, false);
        return new PlantOrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantOrderViewHolder holder, int position) {
        PlantOrder plantOrder = plantOrders.get(position);

        // Set orderCode and clientName
        holder.orderCodeTextView.setText("ID: " + plantOrder.getOrderCode());
        holder.clientNameTextView.setText(plantOrder.getCliente().getName());

        // Set the status
        String statusText = plantOrder.isStatus() ? "Completado" : "Pendiente";
        holder.statusTextView.setText(statusText);

        // Set the timestamp if you want to show it
        // holder.timestampTextView.setText(plantOrder.getTimestamp().toString());
    }

    @Override
    public int getItemCount() {
        return plantOrders.size();
    }

    public static class PlantOrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderCodeTextView;
        TextView clientNameTextView;
        TextView statusTextView;

        public PlantOrderViewHolder(View itemView) {
            super(itemView);
            orderCodeTextView = itemView.findViewById(R.id.order_code);
            clientNameTextView = itemView.findViewById(R.id.user_order);
            statusTextView = itemView.findViewById(R.id.order_status);
        }
    }
}
