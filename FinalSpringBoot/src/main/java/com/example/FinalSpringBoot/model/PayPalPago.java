package com.example.FinalSpringBoot.model;

import com.example.FinalSpringBoot.enums.TipoMetodoPago;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "paypal_pago")
@DiscriminatorValue("PAYPAL")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PayPalPago extends MetodoPago {

    @NotBlank(message = "El email de PayPal es obligatorio")
    @Email(message = "El email debe ser válido")
    @Column(name = "email_paypal", nullable = false, length = 150)
    private String emailPaypal;

    public PayPalPago(String titular, String emailPaypal) {
        super(null, TipoMetodoPago.PAYPAL, titular);
        this.emailPaypal = emailPaypal;
    }
}
