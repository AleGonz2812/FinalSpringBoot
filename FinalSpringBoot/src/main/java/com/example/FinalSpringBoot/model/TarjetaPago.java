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

import java.time.YearMonth;

@Entity
@Table(name = "tarjeta_pago")
@DiscriminatorValue("TARJETA")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TarjetaPago extends MetodoPago {

    @NotBlank(message = "El número de tarjeta es obligatorio")
    @Pattern(regexp = "\\d{16}", message = "El número de tarjeta debe tener 16 dígitos")
    @Column(name = "numero_tarjeta", nullable = false, length = 16)
    private String numeroTarjeta;

    @NotBlank(message = "El CVV es obligatorio")
    @Pattern(regexp = "\\d{3,4}", message = "El CVV debe tener 3 o 4 dígitos")
    @Column(nullable = false, length = 4)
    private String cvv;

    @Column(name = "fecha_expiracion", nullable = false)
    private YearMonth fechaExpiracion;

    public TarjetaPago(String titular, String numeroTarjeta, String cvv, YearMonth fechaExpiracion) {
        super(null, TipoMetodoPago.TARJETA, titular);
        this.numeroTarjeta = numeroTarjeta;
        this.cvv = cvv;
        this.fechaExpiracion = fechaExpiracion;
    }
}
