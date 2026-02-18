package com.example.FinalSpringBoot.repository;

import com.example.FinalSpringBoot.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    // Buscar facturas de una suscripción específica
    List<Factura> findBySuscripcionId(Long suscripcionId);

    // Buscar facturas de un usuario (a través de suscripción)
    @Query("SELECT f FROM Factura f WHERE f.suscripcion.usuario.id = :usuarioId")
    List<Factura> findByUsuarioId(Long usuarioId);

    // Filtrar facturas por rango de fechas
    List<Factura> findByFechaEmisionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Filtrar facturas por monto total mayor a cierto valor
    List<Factura> findByMontoTotalGreaterThan(BigDecimal monto);

    // Filtrar facturas por monto total menor a cierto valor
    List<Factura> findByMontoTotalLessThan(BigDecimal monto);

    // Buscar facturas de un usuario ordenadas por fecha (las más recientes primero)
    @Query("SELECT f FROM Factura f WHERE f.suscripcion.usuario.id = :usuarioId ORDER BY f.fechaEmision DESC")
    List<Factura> findByUsuarioIdOrderByFechaEmisionDesc(Long usuarioId);

    // Obtener facturas entre un rango de fechas y un monto específico
    @Query("SELECT f FROM Factura f WHERE f.fechaEmision BETWEEN :fechaInicio AND :fechaFin AND f.montoTotal BETWEEN :montoMin AND :montoMax")
    List<Factura> findByFechaYMonto(LocalDateTime fechaInicio, LocalDateTime fechaFin, BigDecimal montoMin, BigDecimal montoMax);
}
