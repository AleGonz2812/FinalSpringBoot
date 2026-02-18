package com.example.FinalSpringBoot.enums;

public enum Rol {
    USER("Usuario"),
    ADMIN("Administrador");

    private final String nombre;

    Rol(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
