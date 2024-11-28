package com.example.projectintegration.models;

public class PAdquridasUsuario {
    private String nombre;
    private String fechaAdquisicion;
    private int imagenResId;

    // Constructor
    public PAdquridasUsuario(String nombre, String fechaAdquisicion, int imagenResId) {
        this.nombre = nombre;
        this.fechaAdquisicion = fechaAdquisicion;
        this.imagenResId = imagenResId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public int getImagenResId() {
        return imagenResId;
    }
}
