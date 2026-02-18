package com.example.FinalSpringBoot.enums;

public enum EstadoSuscripcion {
    ACTIVA("Activa"),
    CANCELADA("Cancelada"),
    MOROSA("Morosa");

    private final String estado;

    EstadoSuscripcion(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }
}
