package com.example.projectintegration.models;

import java.util.List;

public class PlantOrder {
    private int orderCode;
    private List<PlantOrderList> plants;

    public PlantOrder() {}

    public PlantOrder(int orderCode, List<PlantOrderList> plants) {
        this.orderCode = orderCode;
        this.plants = plants;
    }

    public int getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(int orderCode) {
        this.orderCode = orderCode;
    }

    public List<PlantOrderList> getPlants() {
        return plants;
    }

    public void setPlants(List<PlantOrderList> plants) {
        this.plants = plants;
    }
}
