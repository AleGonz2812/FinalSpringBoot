package com.example.saasfinal.model.entity;

import com.example.saasfinal.model.enums.TipoPlan;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Plan - Representa los diferentes niveles de suscripción
 * Usa el Enum TipoPlan para definir los tipos
 */
@Entity
@Table(name = "planes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El tipo de plan es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private TipoPlan tipo;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que cero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "max_usuarios")
    private Integer maxUsuarios;

    @Column(name = "almacenamiento_gb")
    private Integer almacenamientoGb;

    @Column(name = "soporte_prioritario")
    private Boolean soportePrioritario = false;

    @Column(name = "activo")
    private Boolean activo = true;

    // Relación 1:N con Suscripción
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<Suscripcion> suscripciones = new ArrayList<>();

    /**
     * Constructor conveniente que inicializa un plan desde el enum
     */
    public Plan(TipoPlan tipo) {
        this.tipo = tipo;
        this.precio = tipo.getPrecioBase();
        initializeFeaturesByType();
    }

    /**
     * Inicializa las características según el tipo de plan
     */
    private void initializeFeaturesByType() {
        switch (tipo) {
            case BASIC:
                this.maxUsuarios = 1;
                this.almacenamientoGb = 10;
                this.soportePrioritario = false;
                this.descripcion = "Plan básico ideal para individuos";
                break;
            case PREMIUM:
                this.maxUsuarios = 10;
                this.almacenamientoGb = 100;
                this.soportePrioritario = true;
                this.descripcion = "Plan premium para pequeños equipos";
                break;
            case ENTERPRISE:
                this.maxUsuarios = -1; // Ilimitado
                this.almacenamientoGb = 1000;
                this.soportePrioritario = true;
                this.descripcion = "Plan enterprise para grandes organizaciones";
                break;
        }
    }
}
