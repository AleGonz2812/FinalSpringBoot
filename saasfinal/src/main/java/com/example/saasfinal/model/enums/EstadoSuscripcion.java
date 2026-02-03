package com.example.saasfinal.model.enums;

/**
 * Estados posibles de una suscripción
 */
public enum EstadoSuscripcion {
    ACTIVA("Activa"),
    CANCELADA("Cancelada"),
    MOROSA("Morosa");

    private final String descripcion;

    EstadoSuscripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
