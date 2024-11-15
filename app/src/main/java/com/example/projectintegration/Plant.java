package com.example.projectintegration;

public class Plant {
    private String name;
    private String description;
    private int quantity;
    private String imageUrl;

    public Plant() {}

    public Plant(String name, String description, int quantity, String imageUrl) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }
    // Getters y Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}