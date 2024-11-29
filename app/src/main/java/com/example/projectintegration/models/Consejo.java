package com.example.projectintegration.models;

public class Consejo {
    private String nombreUsuario;
    private String textoConsejo;
    private int fotoPerfil;

    public Consejo(String nombreUsuario, String textoConsejo, int fotoPerfil) {
        this.nombreUsuario = nombreUsuario;
        this.textoConsejo = textoConsejo;
        this.fotoPerfil = fotoPerfil;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getTextoConsejo() {
        return textoConsejo;
    }

    public int getFotoPerfil() {
        return fotoPerfil;
    }
}
