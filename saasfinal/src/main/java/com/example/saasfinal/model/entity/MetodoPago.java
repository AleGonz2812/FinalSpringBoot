package com.example.saasfinal.model.entity;

import com.example.saasfinal.model.enums.TipoPago;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase base abstracta para los métodos de pago
 * Usa herencia de tabla única (SINGLE_TABLE) con discriminador
 * Esto es un ejemplo de JPA AVANZADO con herencia
 */
@Entity
@Table(name = "metodos_pago")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_pago", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Audited
public abstract class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El tipo de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoPago tipo;

    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "predeterminado")
    private Boolean predeterminado = false;

    // Relación 1:N con Factura
    @OneToMany(mappedBy = "metodoPago")
    private List<Factura> facturas = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }

    /**
     * Método abstracto que cada tipo de pago debe implementar
     * para validar sus datos específicos
     */
    public abstract boolean validarDatos();
}
