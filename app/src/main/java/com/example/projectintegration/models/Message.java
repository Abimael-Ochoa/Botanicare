package com.example.projectintegration.models;

public class Message {
    private String sender;
    private String content; // Contenido del mensaje
    private String receiver; // Nuevo campo: receptor del mensaje
    private long timestamp; // Marca de tiempo para ordenarlo
    private boolean isRead;  // Nuevo campo para indicar si el mensaje ha sido leído

    // Constructor vacío requerido por Firebase
    public Message() {
    }

    // Constructor con el campo isRead
    public Message(String sender, String receiver, String content, long timestamp, boolean isRead) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }


    // Getters y setters
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }
}
