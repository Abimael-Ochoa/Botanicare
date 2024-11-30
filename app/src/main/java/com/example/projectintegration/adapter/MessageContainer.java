package com.example.projectintegration.adapter;

import com.example.projectintegration.models.Message;

import java.util.List;

public class MessageContainer {
    private List<Message> messages;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}