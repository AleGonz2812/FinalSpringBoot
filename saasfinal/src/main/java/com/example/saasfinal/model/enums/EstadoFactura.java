package com.example.saasfinal.model.enums;

/**
 * Estados posibles de una factura
 */
public enum EstadoFactura {
    PENDIENTE("Pendiente"),
    PAGADA("Pagada"),
    VENCIDA("Vencida"),
    CANCELADA("Cancelada");

    private final String descripcion;

    EstadoFactura(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
