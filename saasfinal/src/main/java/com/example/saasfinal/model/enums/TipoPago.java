package com.example.saasfinal.model.enums;

/**
 * Tipos de métodos de pago disponibles
 */
public enum TipoPago {
    TARJETA("Tarjeta de Crédito/Débito"),
    PAYPAL("PayPal"),
    TRANSFERENCIA("Transferencia Bancaria");

    private final String descripcion;

    TipoPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
