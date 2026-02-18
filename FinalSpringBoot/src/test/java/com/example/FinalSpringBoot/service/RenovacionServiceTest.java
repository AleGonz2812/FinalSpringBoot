package com.example.FinalSpringBoot.service;

import com.example.FinalSpringBoot.enums.EstadoSuscripcion;
import com.example.FinalSpringBoot.enums.Rol;
import com.example.FinalSpringBoot.enums.TipoPlan;
import com.example.FinalSpringBoot.model.Perfil;
import com.example.FinalSpringBoot.model.Plan;
import com.example.FinalSpringBoot.model.Suscripcion;
import com.example.FinalSpringBoot.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RenovacionServiceTest {

    @Mock
    private SuscripcionService suscripcionService;

    @InjectMocks
    private RenovacionService renovacionService;

    private Usuario usuario1;
    private Usuario usuario2;
    private Plan plan;
    private Suscripcion suscripcion1;
    private Suscripcion suscripcion2;

    @BeforeEach
    void setUp() {
        // Usuario 1
        usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setUsername("user1");
        usuario1.setEmail("user1@test.com");
        usuario1.setRol(Rol.USER);

        Perfil perfil1 = new Perfil();
        perfil1.setId(1L);
        perfil1.setNombre("User");
        perfil1.setApellido("One");
        perfil1.setPais("España");
        perfil1.setUsuario(usuario1);
        usuario1.setPerfil(perfil1);

        // Usuario 2
        usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setUsername("user2");
        usuario2.setEmail("user2@test.com");
        usuario2.setRol(Rol.USER);

        Perfil perfil2 = new Perfil();
        perfil2.setId(2L);
        perfil2.setNombre("User");
        perfil2.setApellido("Two");
        perfil2.setPais("México");
        perfil2.setUsuario(usuario2);
        usuario2.setPerfil(perfil2);

        // Plan
        plan = new Plan();
        plan.setId(1L);
        plan.setNombre(TipoPlan.BASIC);
        plan.setPrecio(new BigDecimal("9.99"));

        // Suscripción 1 (para renovar)
        suscripcion1 = new Suscripcion();
        suscripcion1.setId(1L);
        suscripcion1.setUsuario(usuario1);
        suscripcion1.setPlan(plan);
        suscripcion1.setEstado(EstadoSuscripcion.ACTIVA);
        suscripcion1.setFechaInicio(LocalDateTime.now().minusDays(30));
        suscripcion1.setFechaFin(LocalDateTime.now().minusDays(1)); // Vencida

        // Suscripción 2 (para renovar)
        suscripcion2 = new Suscripcion();
        suscripcion2.setId(2L);
        suscripcion2.setUsuario(usuario2);
        suscripcion2.setPlan(plan);
        suscripcion2.setEstado(EstadoSuscripcion.ACTIVA);
        suscripcion2.setFechaInicio(LocalDateTime.now().minusDays(30));
        suscripcion2.setFechaFin(LocalDateTime.now().minusDays(1)); // Vencida
    }

    @Test
    void testRenovarSuscripcionesAutomaticamente_TodasExitosas() {
        // Given
        List<Suscripcion> suscripciones = Arrays.asList(suscripcion1, suscripcion2);
        when(suscripcionService.obtenerSuscripcionesParaRenovar()).thenReturn(suscripciones);
        when(suscripcionService.renovarSuscripcion(1L)).thenReturn(suscripcion1);
        when(suscripcionService.renovarSuscripcion(2L)).thenReturn(suscripcion2);

        // When
        renovacionService.renovarSuscripcionesAutomaticamente();

        // Then
        verify(suscripcionService, times(1)).obtenerSuscripcionesParaRenovar();
        verify(suscripcionService, times(1)).renovarSuscripcion(1L);
        verify(suscripcionService, times(1)).renovarSuscripcion(2L);
        verify(suscripcionService, never()).marcarComoMorosa(anyLong());
    }

    @Test
    void testRenovarSuscripcionesAutomaticamente_UnaFalla() {
        // Given
        List<Suscripcion> suscripciones = Arrays.asList(suscripcion1, suscripcion2);
        when(suscripcionService.obtenerSuscripcionesParaRenovar()).thenReturn(suscripciones);
        when(suscripcionService.renovarSuscripcion(1L)).thenReturn(suscripcion1);
        when(suscripcionService.renovarSuscripcion(2L))
                .thenThrow(new RuntimeException("Error de pago"));
        when(suscripcionService.marcarComoMorosa(2L)).thenReturn(suscripcion2);

        // When
        renovacionService.renovarSuscripcionesAutomaticamente();

        // Then
        verify(suscripcionService, times(1)).obtenerSuscripcionesParaRenovar();
        verify(suscripcionService, times(1)).renovarSuscripcion(1L);
        verify(suscripcionService, times(1)).renovarSuscripcion(2L);
        verify(suscripcionService, times(1)).marcarComoMorosa(2L);
    }

    @Test
    void testRenovarSuscripcionesAutomaticamente_TodasFallan() {
        // Given
        List<Suscripcion> suscripciones = Arrays.asList(suscripcion1, suscripcion2);
        when(suscripcionService.obtenerSuscripcionesParaRenovar()).thenReturn(suscripciones);
        when(suscripcionService.renovarSuscripcion(anyLong()))
                .thenThrow(new RuntimeException("Error de pago"));
        when(suscripcionService.marcarComoMorosa(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 1L ? suscripcion1 : suscripcion2;
        });

        // When
        renovacionService.renovarSuscripcionesAutomaticamente();

        // Then
        verify(suscripcionService, times(1)).obtenerSuscripcionesParaRenovar();
        verify(suscripcionService, times(1)).renovarSuscripcion(1L);
        verify(suscripcionService, times(1)).renovarSuscripcion(2L);
        verify(suscripcionService, times(1)).marcarComoMorosa(1L);
        verify(suscripcionService, times(1)).marcarComoMorosa(2L);
    }

    @Test
    void testRenovarSuscripcionesAutomaticamente_SinSuscripciones() {
        // Given
        when(suscripcionService.obtenerSuscripcionesParaRenovar())
                .thenReturn(Collections.emptyList());

        // When
        renovacionService.renovarSuscripcionesAutomaticamente();

        // Then
        verify(suscripcionService, times(1)).obtenerSuscripcionesParaRenovar();
        verify(suscripcionService, never()).renovarSuscripcion(anyLong());
        verify(suscripcionService, never()).marcarComoMorosa(anyLong());
    }

    @Test
    void testRenovarSuscripcionesAutomaticamente_ErrorAlMarcarMorosa() {
        // Given
        List<Suscripcion> suscripciones = Arrays.asList(suscripcion1);
        when(suscripcionService.obtenerSuscripcionesParaRenovar()).thenReturn(suscripciones);
        when(suscripcionService.renovarSuscripcion(1L))
                .thenThrow(new RuntimeException("Error de pago"));
        when(suscripcionService.marcarComoMorosa(1L))
                .thenThrow(new RuntimeException("Error al marcar como morosa"));

        // When
        renovacionService.renovarSuscripcionesAutomaticamente();

        // Then
        verify(suscripcionService, times(1)).renovarSuscripcion(1L);
        verify(suscripcionService, times(1)).marcarComoMorosa(1L);
        // No debe lanzar excepción, solo loggear el error
    }

    @Test
    void testForzarRenovaciones() {
        // Given
        List<Suscripcion> suscripciones = Arrays.asList(suscripcion1);
        when(suscripcionService.obtenerSuscripcionesParaRenovar()).thenReturn(suscripciones);
        when(suscripcionService.renovarSuscripcion(1L)).thenReturn(suscripcion1);

        // When
        renovacionService.forzarRenovaciones();

        // Then
        verify(suscripcionService, times(1)).obtenerSuscripcionesParaRenovar();
        verify(suscripcionService, times(1)).renovarSuscripcion(1L);
    }

    @Test
    void testRenovarSuscripcionesAutomaticamente_MultiplesRenovacionesYFallos() {
        // Given
        Suscripcion suscripcion3 = new Suscripcion();
        suscripcion3.setId(3L);
        suscripcion3.setUsuario(usuario1);
        suscripcion3.setPlan(plan);

        List<Suscripcion> suscripciones = Arrays.asList(suscripcion1, suscripcion2, suscripcion3);
        when(suscripcionService.obtenerSuscripcionesParaRenovar()).thenReturn(suscripciones);
        
        // Suscripción 1: Éxito
        when(suscripcionService.renovarSuscripcion(1L)).thenReturn(suscripcion1);
        
        // Suscripción 2: Fallo, se marca como morosa
        when(suscripcionService.renovarSuscripcion(2L))
                .thenThrow(new RuntimeException("Fallo de pago"));
        when(suscripcionService.marcarComoMorosa(2L)).thenReturn(suscripcion2);
        
        // Suscripción 3: Éxito
        when(suscripcionService.renovarSuscripcion(3L)).thenReturn(suscripcion3);

        // When
        renovacionService.renovarSuscripcionesAutomaticamente();

        // Then
        verify(suscripcionService, times(3)).renovarSuscripcion(anyLong());
        verify(suscripcionService, times(1)).marcarComoMorosa(2L);
        verify(suscripcionService, never()).marcarComoMorosa(1L);
        verify(suscripcionService, never()).marcarComoMorosa(3L);
    }
}
