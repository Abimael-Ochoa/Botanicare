package com.example.projectintegration.models;

public class Plant {
    private String name;
    private String description;
    private int quantity;
    private String imageUrl;
    private String scientificName; // Agregar
    private String care; // Agregar

    public Plant() {}

    public Plant(String name, String description, int quantity, String imageUrl,String scientificName,String care) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.scientificName = scientificName;
        this.care = care;
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

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getCare() {
        return care;
    }

    public void setCare(String care) {
        this.care = care;
    }
}