package com.example.projectintegration.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectintegration.R;

import com.example.projectintegration.models.UserChat;


import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private ArrayList<UserChat> userList;
    private OnUserClickListener onUserClickListener;

    public UserAdapter(ArrayList<UserChat> userList) {
        this.userList = userList;
    }

    // Setter para el listener
    public void setOnUserClickListener(OnUserClickListener listener) {
        this.onUserClickListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserChat user = userList.get(position);
        holder.tvName.setText(user.getName());
        holder.tvUnreadMessages.setText(String.valueOf(user.getUnreadMessages()));

        // Configurar el clic del elemento
        holder.itemView.setOnClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvUnreadMessages;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_user_name);
            tvUnreadMessages = itemView.findViewById(R.id.tv_notification_count);
        }
    }

    public interface OnUserClickListener {
        void onUserClick(UserChat user);
    }
}
