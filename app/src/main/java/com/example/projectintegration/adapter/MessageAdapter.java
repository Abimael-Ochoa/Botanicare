package com.example.projectintegration.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectintegration.R;
import com.example.projectintegration.models.Message;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;     // Mensaje enviado por el usuario
    private static final int VIEW_TYPE_RECEIVED = 2; // Mensaje recibido por el usuario

    private ArrayList<Message> messageList;
    private String currentUserId; // UID del usuario actual

    public MessageAdapter(ArrayList<Message> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);

        // Determina si el mensaje fue enviado por el usuario actual o es recibido
        if (message.getSender().equals(currentUserId)) {
            return VIEW_TYPE_SENT; // Mensaje enviado
        } else {
            return VIEW_TYPE_RECEIVED; // Mensaje recibido
        }
    }
//dsfsdfd
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder para mensajes enviados por el usuario actual
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;

        SentMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_message_sent);
        }

        void bind(Message message) {
            messageText.setText(message.getContent()); // Mostrar el contenido del mensaje enviado
        }
    }

    // ViewHolder para mensajes recibidos (de otros usuarios o admin)
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;

        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_message_received);
        }

        void bind(Message message) {
            messageText.setText(message.getContent()); // Mostrar el contenido del mensaje recibido
        }
    }
}
