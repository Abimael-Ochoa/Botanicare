package com.example.projectintegration.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projectintegration.R;
import com.example.projectintegration.models.PlantOrder;
import com.example.projectintegration.models.PlantOrderList;

import java.util.List;

public class PlantOrderAdapter extends RecyclerView.Adapter<PlantOrderAdapter.PlantOrderViewHolder> {

    private List<PlantOrder> plantOrders;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int orderCode);
    }

    public PlantOrderAdapter(List<PlantOrder> plantOrders, OnItemClickListener onItemClickListener){
        this.plantOrders = plantOrders;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public PlantOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new PlantOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlantOrderViewHolder holder, int position) {
        PlantOrder plantOrder = plantOrders.get(position);
        holder.orderCodeTextView.setText("ID: " + plantOrder.getOrderCode());
        holder.userNameTextView.setText(plantOrder.getCliente().getName());
        holder.status.setText(plantOrder.getStatus());
        holder.status.setTextColor(plantOrder.getStatus().equals("Pendiente") ? Color.parseColor("#FF5722") : Color.parseColor("#4CAF50"));


        // Setup click listener for the item
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



