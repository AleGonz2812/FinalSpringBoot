package com.example.saasfinal.model.entity;

import com.example.saasfinal.model.enums.TipoPago;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Método de pago mediante PayPal
 * Hereda de MetodoPago
 */
@Entity
@DiscriminatorValue("PAYPAL")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PagoPayPal extends MetodoPago {

    @NotBlank(message = "El email de PayPal es obligatorio")
    @Email(message = "El email de PayPal debe ser válido")
    @Column(name = "email_paypal", length = 100)
    private String emailPayPal;

    @Column(name = "id_cuenta_paypal", length = 100)
    private String idCuentaPayPal;

    @Column(name = "verificado")
    private Boolean verificado = false;

    public PagoPayPal(String emailPayPal) {
        super();
        setTipo(TipoPago.PAYPAL);
        this.emailPayPal = emailPayPal;
    }

    @Override
    public boolean validarDatos() {
        // Validar que el email no esté vacío y que la cuenta esté verificada
        return emailPayPal != null && !emailPayPal.isEmpty() && verificado;
    }
}
