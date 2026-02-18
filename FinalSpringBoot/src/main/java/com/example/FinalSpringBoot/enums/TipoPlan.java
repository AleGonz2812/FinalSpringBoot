package com.example.FinalSpringBoot.enums;

public enum TipoPlan {
    BASIC("Basic"),
    PREMIUM("Premium"),
    ENTERPRISE("Enterprise");

    private final String nombre;

    TipoPlan(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
