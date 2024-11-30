package com.example.projectintegration.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projectintegration.R;
import com.example.projectintegration.models.PlantOrderList;

import java.util.List;

public class PlantOrderListAdapter extends RecyclerView.Adapter<PlantOrderListAdapter.PlantOrderListViewHolder> {

    private List<PlantOrderList> plantItems;

    public PlantOrderListAdapter(List<PlantOrderList> plantItems) {
        this.plantItems = plantItems;
    }

    @Override
    public PlantOrderListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket_plan, parent, false);
        return new PlantOrderListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlantOrderListViewHolder holder, int position) {
        PlantOrderList plant = plantItems.get(position);
        holder.plantNameTextView.setText(plant.getPlantName());
        holder.quantityTextView.setText("Cantidad: " + plant.getQuantity());
    }

    @Override
    public int getItemCount() {
        return plantItems.size();
    }

    public static class PlantOrderListViewHolder extends RecyclerView.ViewHolder {

        TextView plantNameTextView;
        TextView quantityTextView;

        public PlantOrderListViewHolder(View itemView) {
            super(itemView);
            plantNameTextView = itemView.findViewById(R.id.tv_nombre_planta);
            quantityTextView = itemView.findViewById(R.id.tv_cantidad_planta);
        }
    }
}
