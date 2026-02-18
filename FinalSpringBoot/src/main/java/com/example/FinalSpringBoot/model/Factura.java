package com.example.FinalSpringBoot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha de emisión es obligatoria")
    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @NotNull(message = "El monto bruto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto bruto debe ser mayor a 0")
    @Column(name = "monto_bruto", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoBruto;

    @NotNull(message = "El impuesto es obligatorio")
    @DecimalMin(value = "0.0", message = "El impuesto no puede ser negativo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal impuesto;

    @NotNull(message = "El monto total es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto total debe ser mayor a 0")
    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @ManyToOne
    @JoinColumn(name = "suscripcion_id", nullable = false)
    private Suscripcion suscripcion;

    @PrePersist
    protected void onCreate() {
        if (fechaEmision == null) {
            fechaEmision = LocalDateTime.now();
        }
    }
}
