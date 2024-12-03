package com.example.projectintegration.models;

import java.util.Date;
import java.util.List;

public class PlantOrder {
    private int orderCode;
    private List<PlantOrderList> plantItems;
    private Cliente cliente; // Nuevo campo para información del cliente
    private boolean status;  // Nuevo campo para el estatus
    private Date timestamp;

    public PlantOrder() {}

    public PlantOrder(int orderCode, List<PlantOrderList> plantItems, Cliente cliente, boolean status, Date timestamp) {
        this.orderCode = orderCode;
        this.plantItems = plantItems;
        this.cliente = cliente;
        this.status = status;
        this.timestamp = timestamp;
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

    public String getStatus() {
       return status ? "Aceptado":"Pendiente";
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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
