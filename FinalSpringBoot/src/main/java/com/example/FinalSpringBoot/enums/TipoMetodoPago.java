package com.example.FinalSpringBoot.enums;

public enum TipoMetodoPago {
    TARJETA("Tarjeta de Crédito/Débito"),
    PAYPAL("PayPal"),
    TRANSFERENCIA("Transferencia Bancaria");

    private final String tipo;

    TipoMetodoPago(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
