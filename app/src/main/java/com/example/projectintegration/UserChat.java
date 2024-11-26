package com.example.projectintegration;

public class UserChat {
    private String name;
    private int unreadMessages;

    // Constructor vacío para Firebase
    public UserChat() {}

    public UserChat(String name,  int unreadMessages) {
        this.name = name;
        this.unreadMessages =  unreadMessages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }


}