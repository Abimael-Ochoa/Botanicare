package com.example.projectintegration.models;

public class PlantOrderList {
    private String plantName;
    private int quantity;

    public PlantOrderList() {}

    public PlantOrderList(String plantName, int quantity) {
        this.plantName = plantName;
        this.quantity = quantity;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
