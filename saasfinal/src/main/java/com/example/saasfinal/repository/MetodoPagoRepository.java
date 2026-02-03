package com.example.saasfinal.repository;

import com.example.saasfinal.model.entity.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
    
    List<MetodoPago> findByUsuarioId(Long usuarioId);
    
    Optional<MetodoPago> findByUsuarioIdAndPredeterminado(Long usuarioId, Boolean predeterminado);
    
    List<MetodoPago> findByUsuarioIdAndActivo(Long usuarioId, Boolean activo);
}
