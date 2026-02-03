package com.example.saasfinal.model.enums;

import java.math.BigDecimal;

/**
 * Tipos de planes disponibles con sus precios base
 */
public enum TipoPlan {
    BASIC("Basic", new BigDecimal("9.99")),
    PREMIUM("Premium", new BigDecimal("29.99")),
    ENTERPRISE("Enterprise", new BigDecimal("99.99"));

    private final String nombre;
    private final BigDecimal precioBase;

    TipoPlan(String nombre, BigDecimal precioBase) {
        this.nombre = nombre;
        this.precioBase = precioBase;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }
}
