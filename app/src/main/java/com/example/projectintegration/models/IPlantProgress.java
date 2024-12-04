package com.example.projectintegration.models;

public class IPlantProgress {
    private String description;
    private String plantName; // Nombre único de la planta
    private String imageUrl; // Almacena URLs de imágenes
    private String uniqueId; // Identificador único de la planta en la base de datos


    // Constructor
    public IPlantProgress(String plantName, String imageUrl, String uniqueId) {
        this.plantName = plantName;
        this.imageUrl = imageUrl;
        this.uniqueId = uniqueId;

    }



    public void setDescription(String description) {
        this.description = description;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
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
    public String getUniqueId() {
        return uniqueId;
    }

}
