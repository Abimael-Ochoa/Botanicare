package com.example.projectintegration.models;

public class IPlantProgress {
    private int imageResId;
    private String description;
    private String plantName; // Nombre único de la planta

    // Constructor
    public IPlantProgress(int imageResId, String description) {
        this.imageResId = imageResId;
        this.description = description;
        this.plantName = plantName;
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


}
