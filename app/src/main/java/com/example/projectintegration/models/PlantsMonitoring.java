package com.example.projectintegration.models;

public class PlantsMonitoring {

    private String imageUrl; // Almacena URLs de imágenes
    private String uniqueId; // Identificador único de la planta en la base de datos
    private String plantNickName;
    private String notes;
    private String progressDate;

    // Constructor
    public PlantsMonitoring( String plantNickName, String imageUrl, String notes, String progressDate, String uniqueId) {
        this.plantNickName = plantNickName != null ? plantNickName : "Sin apodo";
        this.imageUrl = imageUrl; // Puede ser null si no hay imagen
        this.notes = notes != null ? notes : "Sin notas";
        this.progressDate = progressDate != null ? progressDate : "Sin fecha registrada";
        this.uniqueId = uniqueId;
    }




    public String getImageUrl() {
        return imageUrl;
    }

    public String getNotes() {
        return notes;
    }

    public String getProgressDate() {
        return progressDate;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getPlantNickName() {
        return plantNickName;
    }
}
