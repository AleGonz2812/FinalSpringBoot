package com.example.saasfinal.model.entity;

import com.example.saasfinal.model.enums.TipoPago;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Método de pago mediante tarjeta de crédito/débito
 * Hereda de MetodoPago
 */
@Entity
@DiscriminatorValue("TARJETA")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PagoTarjeta extends MetodoPago {

    @NotBlank(message = "El número de tarjeta es obligatorio")
    @Pattern(regexp = "\\d{16}", message = "El número de tarjeta debe tener 16 dígitos")
    @Column(name = "numero_tarjeta", length = 16)
    private String numeroTarjeta;

    @NotBlank(message = "El titular es obligatorio")
    @Column(name = "titular", length = 100)
    private String titular;

    @NotBlank(message = "La fecha de vencimiento es obligatoria")
    @Column(name = "fecha_vencimiento", length = 7) // Formato MM/YYYY
    private String fechaVencimiento;

    @NotBlank(message = "El CVV es obligatorio")
    @Pattern(regexp = "\\d{3,4}", message = "El CVV debe tener 3 o 4 dígitos")
    @Column(name = "cvv", length = 4)
    private String cvv;

    public PagoTarjeta(String numeroTarjeta, String titular, String fechaVencimiento, String cvv) {
        super();
        setTipo(TipoPago.TARJETA);
        this.numeroTarjeta = numeroTarjeta;
        this.titular = titular;
        this.fechaVencimiento = fechaVencimiento;
        this.cvv = cvv;
    }

    @Override
    public boolean validarDatos() {
        // Validar que la tarjeta no haya expirado
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
            YearMonth vencimiento = YearMonth.parse(fechaVencimiento, formatter);
            return vencimiento.isAfter(YearMonth.now()) || vencimiento.equals(YearMonth.now());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Retorna los últimos 4 dígitos de la tarjeta para mostrar al usuario
     */
    public String getUltimosDigitos() {
        if (numeroTarjeta != null && numeroTarjeta.length() >= 4) {
            return "**** **** **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4);
        }
        return "****";
    }
}
