package com.example.FinalSpringBoot.model;

import com.example.FinalSpringBoot.enums.TipoMetodoPago;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "metodos_pago")
@Inheritance(strategy = InheritanceType.JOINED)  
@DiscriminatorColumn(name = "tipo_metodo", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El tipo de método de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoMetodoPago tipo;

    @NotBlank(message = "El titular es obligatorio")
    @Column(nullable = false, length = 150)
    private String titular;
}
