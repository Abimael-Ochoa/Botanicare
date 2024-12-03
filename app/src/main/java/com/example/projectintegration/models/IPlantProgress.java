package com.example.projectintegration.models;

public class IPlantProgress {
    private int imageResId;
    private String description;
    private String plantName; // Nombre único de la planta
    private String imageUrl; // Almacena URLs de imágenes


    // Constructor
    public IPlantProgress(String plantName, String imageUrl) {
        this.imageResId = imageResId;
        this.description = description;
        this.plantName = plantName;
        this.imageUrl = imageUrl;

    }

    // Getters
    public int getImageResId() {
        return imageResId;
    }

    public String getDescription() {
        return description;
    }

    public String getPlantName() {
        return plantName;
    }
    public String getImageUrl() {
        return imageUrl;
    }


}
