package com.example.FinalSpringBoot.repository;

import com.example.FinalSpringBoot.enums.EstadoSuscripcion;
import com.example.FinalSpringBoot.model.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {

    List<Suscripcion> findByUsuarioId(Long usuarioId);

    Optional<Suscripcion> findByUsuarioIdAndEstado(Long usuarioId, EstadoSuscripcion estado);

    List<Suscripcion> findByEstado(EstadoSuscripcion estado);

    @Query("SELECT s FROM Suscripcion s WHERE s.fechaFin <= :fecha AND s.estado = :estado")
    List<Suscripcion> findSuscripcionesParaRenovar(LocalDateTime fecha, EstadoSuscripcion estado);

    List<Suscripcion> findByPlanIdAndEstado(Long planId, EstadoSuscripcion estado);
}
