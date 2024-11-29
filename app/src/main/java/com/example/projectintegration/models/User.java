package com.example.projectintegration.models;

public class User {
    private String name;
    private String phone;
    private String address;
    private String email;
    private int unreadMessages;

    // Constructor vacío para Firebase
    public User() {}

    public User(String name, String phone, String address, String email,int unreadMessages) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.unreadMessages =  unreadMessages;
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

}