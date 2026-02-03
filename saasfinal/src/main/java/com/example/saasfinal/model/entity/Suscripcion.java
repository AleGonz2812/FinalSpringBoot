package com.example.saasfinal.model.entity;

import com.example.saasfinal.model.enums.EstadoSuscripcion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Suscripción - Representa la suscripción de un usuario a un plan
 * @Audited permite rastrear TODOS los cambios de plan y estado
 */
@Entity
@Table(name = "suscripciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Audited // ¡CRÍTICO! Permite ver el histórico de cambios de plan
public class Suscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación N:1 con Usuario
    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relación N:1 con Plan
    @NotNull(message = "El plan es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSuscripcion estado;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "fecha_proxima_renovacion")
    private LocalDate fechaProximaRenovacion;

    @Column(name = "fecha_cambio_plan")
    private LocalDateTime fechaCambioPlan;

    @Column(name = "precio_actual", precision = 10, scale = 2)
    private BigDecimal precioActual;

    @Column(name = "auto_renovacion")
    private Boolean autoRenovacion = true;

    // Relación 1:N con Factura
    @OneToMany(mappedBy = "suscripcion", cascade = CascadeType.ALL)
    private List<Factura> facturas = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now();
        }
        if (estado == null) {
            estado = EstadoSuscripcion.ACTIVA;
        }
        if (precioActual == null && plan != null) {
            precioActual = plan.getPrecio();
        }
        calcularProximaRenovacion();
    }

    @PreUpdate
    protected void onUpdate() {
        if (fechaCambioPlan == null && plan != null) {
            fechaCambioPlan = LocalDateTime.now();
        }
    }

    /**
     * Calcula la fecha de próxima renovación (30 días desde la fecha de inicio)
     */
    public void calcularProximaRenovacion() {
        if (fechaInicio != null) {
            this.fechaProximaRenovacion = fechaInicio.plusDays(30);
        }
    }

    /**
     * Método helper para añadir facturas
     */
    public void addFactura(Factura factura) {
        facturas.add(factura);
        factura.setSuscripcion(this);
    }
}
