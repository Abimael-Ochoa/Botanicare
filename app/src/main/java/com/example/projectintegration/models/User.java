package com.example.projectintegration.models;

public class User {


    private String id;
    private String name;
    private String phone;
    private String address;
    private String email;
    private int unreadMessages;
    private long lastMessageTimestamp;

    // Constructor vacío para Firebase
    public User() {}

    public User(String name, String phone, String address, String email,int unreadMessages) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.unreadMessages =  unreadMessages;
    }

    // Constructor con solo nombre y mensajes
    public User(String name, int unreadMessages) {
        this.name = name;
        this.unreadMessages = unreadMessages;
    }

    // Constructor con solo nombre, mensajes y id
    public User(String name, int unreadMessages, String id) {
        this.name = name;
        this.unreadMessages = unreadMessages;
        this.id = id;
    }

    // Constructor con solo nombre, mensajes y id y email
    public User(String name, int unreadMessages, String id,String email) {
        this.name = name;
        this.unreadMessages = unreadMessages;
        this.id = id;
        this.email = email;
    }

    // ③ Métodos nuevos para el timestamp
    public long getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(long lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}