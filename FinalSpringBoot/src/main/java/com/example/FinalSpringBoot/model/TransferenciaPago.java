package com.example.FinalSpringBoot.model;

import com.example.FinalSpringBoot.enums.TipoMetodoPago;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transferencia_pago")
@DiscriminatorValue("TRANSFERENCIA")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaPago extends MetodoPago {

    @NotBlank(message = "El IBAN es obligatorio")
    @Pattern(regexp = "^[A-Z]{2}\\d{22}$", message = "El IBAN debe tener formato válido (EJ: ES1234567890123456789012)")
    @Column(nullable = false, length = 24)
    private String iban;

    @NotBlank(message = "El banco es obligatorio")
    @Column(nullable = false, length = 150)
    private String banco;

    public TransferenciaPago(String titular, String iban, String banco) {
        super(null, TipoMetodoPago.TRANSFERENCIA, titular);
        this.iban = iban;
        this.banco = banco;
    }
}
