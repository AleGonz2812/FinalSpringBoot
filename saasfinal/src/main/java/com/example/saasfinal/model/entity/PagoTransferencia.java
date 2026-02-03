package com.example.saasfinal.model.entity;

import com.example.saasfinal.model.enums.TipoPago;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Método de pago mediante transferencia bancaria
 * Hereda de MetodoPago
 */
@Entity
@DiscriminatorValue("TRANSFERENCIA")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PagoTransferencia extends MetodoPago {

    @NotBlank(message = "El nombre del banco es obligatorio")
    @Column(name = "nombre_banco", length = 100)
    private String nombreBanco;

    @NotBlank(message = "El número de cuenta es obligatorio")
    @Column(name = "numero_cuenta", length = 50)
    private String numeroCuenta;

    @NotBlank(message = "El titular de la cuenta es obligatorio")
    @Column(name = "titular_cuenta", length = 100)
    private String titularCuenta;

    @Column(name = "codigo_swift", length = 20)
    private String codigoSwift;

    public PagoTransferencia(String nombreBanco, String numeroCuenta, String titularCuenta) {
        super();
        setTipo(TipoPago.TRANSFERENCIA);
        this.nombreBanco = nombreBanco;
        this.numeroCuenta = numeroCuenta;
        this.titularCuenta = titularCuenta;
    }

    @Override
    public boolean validarDatos() {
        // Validación básica de que los campos obligatorios no estén vacíos
        return nombreBanco != null && !nombreBanco.isEmpty()
                && numeroCuenta != null && !numeroCuenta.isEmpty()
                && titularCuenta != null && !titularCuenta.isEmpty();
    }

    /**
     * Retorna la cuenta parcialmente oculta
     */
    public String getCuentaOculta() {
        if (numeroCuenta != null && numeroCuenta.length() > 4) {
            return "****" + numeroCuenta.substring(numeroCuenta.length() - 4);
        }
        return "****";
    }
}
