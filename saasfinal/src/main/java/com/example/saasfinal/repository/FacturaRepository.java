package com.example.saasfinal.repository;

import com.example.saasfinal.model.entity.Factura;
import com.example.saasfinal.model.enums.EstadoFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    
    List<Factura> findBySuscripcionId(Long suscripcionId);
    
    List<Factura> findBySuscripcionUsuarioId(Long usuarioId);
    
    List<Factura> findByEstado(EstadoFactura estado);
    
    List<Factura> findByFechaEmisionBetween(LocalDate inicio, LocalDate fin);
}
