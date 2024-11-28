package com.example.projectintegration.models;

import java.util.List;

public class PlantOrder {
    private int orderCode;
    private List<PlantOrderList> plantItems;
    private Cliente cliente; // Nuevo campo para información del cliente
    private boolean status;  // Nuevo campo para el estatus

    public PlantOrder() {}

    public PlantOrder(int orderCode, List<PlantOrderList> plantItems, Cliente cliente, boolean status) {
        this.orderCode = orderCode;
        this.plantItems = plantItems;
        this.cliente = cliente;
        this.status = status;
    }

    public int getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(int orderCode) {
        this.orderCode = orderCode;
    }

    public List<PlantOrderList> getPlantItems() {
        return plantItems;
    }

    public void setPlantItems(List<PlantOrderList> plantItems) {
        this.plantItems = plantItems;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    // Clase anidada para la información del cliente
    public static class Cliente {
        private String name;

        public Cliente() {}

        public Cliente(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
