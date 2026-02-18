package com.example.FinalSpringBoot.service;

import com.example.FinalSpringBoot.enums.EstadoSuscripcion;
import com.example.FinalSpringBoot.model.Plan;
import com.example.FinalSpringBoot.model.Suscripcion;
import com.example.FinalSpringBoot.model.Usuario;
import com.example.FinalSpringBoot.repository.SuscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SuscripcionService {

    private final SuscripcionRepository suscripcionRepository;
    private final FacturaService facturaService;

    public List<Suscripcion> obtenerTodasLasSuscripciones() {
        return suscripcionRepository.findAll();
    }

    public Optional<Suscripcion> obtenerSuscripcionPorId(Long id) {
        return suscripcionRepository.findById(id);
    }

    
    public List<Suscripcion> obtenerSuscripcionesPorUsuario(Long usuarioId) {
        return suscripcionRepository.findByUsuarioId(usuarioId);
    }

    public Optional<Suscripcion> obtenerSuscripcionActiva(Long usuarioId) {
        return suscripcionRepository.findByUsuarioIdAndEstado(usuarioId, EstadoSuscripcion.ACTIVA);
    }

    public List<Suscripcion> obtenerPorEstado(EstadoSuscripcion estado) {
        return suscripcionRepository.findByEstado(estado);
    }

    
    @Transactional
    public Suscripcion crearSuscripcion(Usuario usuario, Plan plan) {
        Optional<Suscripcion> suscripcionActiva = obtenerSuscripcionActiva(usuario.getId());
        if (suscripcionActiva.isPresent()) {
            throw new RuntimeException("El usuario ya tiene una suscripción activa");
        }

        Suscripcion suscripcion = new Suscripcion();
        suscripcion.setUsuario(usuario);
        suscripcion.setPlan(plan);
        suscripcion.setFechaInicio(LocalDateTime.now());
        suscripcion.setFechaFin(LocalDateTime.now().plusDays(30)); // 30 días de suscripción
        suscripcion.setEstado(EstadoSuscripcion.ACTIVA);

        suscripcion = suscripcionRepository.save(suscripcion);

        facturaService.generarFactura(suscripcion, plan.getPrecio());

        return suscripcion;
    }

    
    @Transactional
    public Suscripcion cambiarPlan(Long usuarioId, Plan nuevoPlan) {
        Suscripcion suscripcionActual = obtenerSuscripcionActiva(usuarioId)
                .orElseThrow(() -> new RuntimeException("No hay suscripción activa para cambiar"));

        Plan planActual = suscripcionActual.getPlan();

        if (planActual.getId().equals(nuevoPlan.getId())) {
            throw new RuntimeException("Ya estás suscrito a este plan");
        }

        boolean esUpgrade = nuevoPlan.getPrecio().compareTo(planActual.getPrecio()) > 0;

        if (esUpgrade) {
            BigDecimal prorrateo = calcularProrrateo(suscripcionActual, nuevoPlan);
            
            suscripcionActual.setPlan(nuevoPlan);
            suscripcionActual = suscripcionRepository.save(suscripcionActual);
            
            facturaService.generarFactura(suscripcionActual, prorrateo);
            
        } else {
            suscripcionActual.setPlan(nuevoPlan);
            suscripcionActual = suscripcionRepository.save(suscripcionActual);
        }

        return suscripcionActual;
    }

   
    private BigDecimal calcularProrrateo(Suscripcion suscripcion, Plan nuevoPlan) {
        Plan planActual = suscripcion.getPlan();
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaFin = suscripcion.getFechaFin();
        
        long diasRestantes = ChronoUnit.DAYS.between(ahora, fechaFin);
        if (diasRestantes < 0) diasRestantes = 0;
        
        BigDecimal diferenciaPrecio = nuevoPlan.getPrecio().subtract(planActual.getPrecio());
        
        BigDecimal factorDias = new BigDecimal(diasRestantes).divide(new BigDecimal(30), 4, RoundingMode.HALF_UP);
        BigDecimal prorrateo = diferenciaPrecio.multiply(factorDias).setScale(2, RoundingMode.HALF_UP);
        
        return prorrateo;
    }

    
    @Transactional
    public Suscripcion renovarSuscripcion(Long suscripcionId) {
        Suscripcion suscripcion = suscripcionRepository.findById(suscripcionId)
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));

        if (suscripcion.getEstado() != EstadoSuscripcion.ACTIVA) {
            throw new RuntimeException("La suscripción no está activa");
        }

        suscripcion.setFechaFin(suscripcion.getFechaFin().plusDays(30));
        suscripcion = suscripcionRepository.save(suscripcion);

        facturaService.generarFactura(suscripcion, suscripcion.getPlan().getPrecio());

        return suscripcion;
    }

    
    public List<Suscripcion> obtenerSuscripcionesParaRenovar() {
        LocalDateTime ahora = LocalDateTime.now();
        return suscripcionRepository.findSuscripcionesParaRenovar(ahora, EstadoSuscripcion.ACTIVA);
    }

    
    @Transactional
    public Suscripcion cancelarSuscripcion(Long suscripcionId) {
        Suscripcion suscripcion = suscripcionRepository.findById(suscripcionId)
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));

        suscripcion.setEstado(EstadoSuscripcion.CANCELADA);
        return suscripcionRepository.save(suscripcion);
    }

   
    @Transactional
    public Suscripcion marcarComoMorosa(Long suscripcionId) {
        Suscripcion suscripcion = suscripcionRepository.findById(suscripcionId)
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));

        suscripcion.setEstado(EstadoSuscripcion.MOROSA);
        return suscripcionRepository.save(suscripcion);
    }
}
