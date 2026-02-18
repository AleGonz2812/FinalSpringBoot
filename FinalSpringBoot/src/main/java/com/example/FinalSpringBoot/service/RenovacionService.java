package com.example.FinalSpringBoot.service;

import com.example.FinalSpringBoot.model.Suscripcion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RenovacionService {

    private final SuscripcionService suscripcionService;

    
    @Scheduled(cron = "0 0 0 * * *") // Cada día a medianoche
    public void renovarSuscripcionesAutomaticamente() {
        log.info("=== Iniciando proceso de renovación automática ===");
        
        List<Suscripcion> suscripcionesParaRenovar = suscripcionService.obtenerSuscripcionesParaRenovar();
        
        log.info("Se encontraron {} suscripciones para renovar", suscripcionesParaRenovar.size());
        
        int renovadas = 0;
        int fallidas = 0;
        
        for (Suscripcion suscripcion : suscripcionesParaRenovar) {
            try {
                suscripcionService.renovarSuscripcion(suscripcion.getId());
                renovadas++;
                log.info("Suscripción {} renovada exitosamente (Usuario: {})", 
                         suscripcion.getId(), 
                         suscripcion.getUsuario().getUsername());
            } catch (Exception e) {
                fallidas++;
                log.error("Error al renovar suscripción {} (Usuario: {}): {}", 
                          suscripcion.getId(), 
                          suscripcion.getUsuario().getUsername(), 
                          e.getMessage());
                
                try {
                    suscripcionService.marcarComoMorosa(suscripcion.getId());
                    log.warn("Suscripción {} marcada como MOROSA", suscripcion.getId());
                } catch (Exception ex) {
                    log.error("Error al marcar suscripción {} como morosa", suscripcion.getId());
                }
            }
        }
        
        log.info("=== Proceso de renovación finalizado ===");
        log.info("Renovadas exitosamente: {}", renovadas);
        log.info("Fallidas: {}", fallidas);
    }

    
    public void forzarRenovaciones() {
        log.info("Forzando renovación manual de suscripciones");
        renovarSuscripcionesAutomaticamente();
    }
}
