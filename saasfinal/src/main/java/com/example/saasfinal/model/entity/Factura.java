package com.example.saasfinal.model.entity;

import com.example.saasfinal.model.enums.EstadoFactura;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad Factura - Representa las facturas generadas automáticamente
 * cada 30 días por las suscripciones
 */
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

    @Column(name = "numero_factura", unique = true, nullable = false, length = 50)
    private String numeroFactura;

    // Relación N:1 con Suscripción
    @NotNull(message = "La suscripción es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suscripcion_id", nullable = false)
    private Suscripcion suscripcion;

    @NotNull(message = "La fecha de emisión es obligatoria")
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @NotNull(message = "El monto base es obligatorio")
    @DecimalMin(value = "0.0", message = "El monto base debe ser mayor o igual a cero")
    @Column(name = "monto_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoBase;

    @Column(name = "impuestos", precision = 10, scale = 2)
    private BigDecimal impuestos = BigDecimal.ZERO;

    @Column(name = "prorrateo", precision = 10, scale = 2)
    private BigDecimal prorrateo = BigDecimal.ZERO;

    @NotNull(message = "El monto total es obligatorio")
    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoFactura estado;

    @Column(length = 500)
    private String concepto;

    // Relación N:1 con MetodoPago (cuando se pague)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metodo_pago_id")
    private MetodoPago metodoPago;

    @PrePersist
    protected void onCreate() {
        if (fechaEmision == null) {
            fechaEmision = LocalDate.now();
        }
        if (fechaVencimiento == null) {
            fechaVencimiento = fechaEmision.plusDays(15); // 15 días para pagar
        }
        if (estado == null) {
            estado = EstadoFactura.PENDIENTE;
        }
        generarNumeroFactura();
        calcularMontoTotal();
    }

    /**
     * Genera un número de factura único
     */
    private void generarNumeroFactura() {
        if (numeroFactura == null) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            this.numeroFactura = "FAC-" + timestamp;
        }
    }

    /**
     * Calcula el monto total: base + impuestos + prorrateo
     */
    public void calcularMontoTotal() {
        this.montoTotal = montoBase
                .add(impuestos != null ? impuestos : BigDecimal.ZERO)
                .add(prorrateo != null ? prorrateo : BigDecimal.ZERO);
    }

    /**
     * Marca la factura como pagada
     */
    public void marcarComoPagada() {
        this.estado = EstadoFactura.PAGADA;
        this.fechaPago = LocalDate.now();
    }
}
