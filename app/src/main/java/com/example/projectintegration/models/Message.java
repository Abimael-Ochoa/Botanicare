package com.example.projectintegration.models;

public class Message {
    private String sender; // Quién envió el mensaje (admin o usuario)
    private String content; // Contenido del mensaje
    private long timestamp; // Marca de tiempo para ordenarlo

    public Message() {
        // Constructor vacío requerido por Firebase
    }

    public Message(String sender, String content, long timestamp) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

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
}
