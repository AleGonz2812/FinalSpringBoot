package com.example.saasfinal.repository;

import com.example.saasfinal.model.entity.Suscripcion;
import com.example.saasfinal.model.enums.EstadoSuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {
    
    List<Suscripcion> findByUsuarioId(Long usuarioId);
    
    Optional<Suscripcion> findByUsuarioIdAndEstado(Long usuarioId, EstadoSuscripcion estado);
    
    List<Suscripcion> findByEstado(EstadoSuscripcion estado);
}
