package com.example.FinalSpringBoot.service;

import com.example.FinalSpringBoot.enums.EstadoSuscripcion;
import com.example.FinalSpringBoot.enums.Rol;
import com.example.FinalSpringBoot.enums.TipoPlan;
import com.example.FinalSpringBoot.model.*;
import com.example.FinalSpringBoot.repository.SuscripcionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class SuscripcionServiceTest {

    @Mock
    private SuscripcionRepository suscripcionRepository;

    @Mock
    private FacturaService facturaService;

    @InjectMocks
    private SuscripcionService suscripcionService;

    private Usuario usuario;
    private Plan planBasic;
    private Plan planPremium;
    private Suscripcion suscripcionActiva;
    private MetodoPago metodoPago;

    @BeforeEach
    void setUp() {
        // Usuario test
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        usuario.setEmail("test@test.com");
        usuario.setRol(Rol.USER);

        Perfil perfil = new Perfil();
        perfil.setId(1L);
        perfil.setNombre("Test");
        perfil.setApellido("User");
        perfil.setPais("España");
        perfil.setUsuario(usuario);
        usuario.setPerfil(perfil);

        // Planes test
        planBasic = new Plan();
        planBasic.setId(1L);
        planBasic.setNombre(TipoPlan.BASIC);
        planBasic.setPrecio(new BigDecimal("9.99"));

        planPremium = new Plan();
        planPremium.setId(2L);
        planPremium.setNombre(TipoPlan.PREMIUM);
        planPremium.setPrecio(new BigDecimal("19.99"));

        // Suscripción activa test
        suscripcionActiva = new Suscripcion();
        suscripcionActiva.setId(1L);
        suscripcionActiva.setUsuario(usuario);
        suscripcionActiva.setPlan(planBasic);
        suscripcionActiva.setEstado(EstadoSuscripcion.ACTIVA);
        suscripcionActiva.setFechaInicio(LocalDateTime.now().minusDays(15));
        suscripcionActiva.setFechaFin(LocalDateTime.now().plusDays(15));

        // Método de pago test
        metodoPago = new TarjetaPago();
        ((TarjetaPago) metodoPago).setNumeroTarjeta("1234567890123456");
        ((TarjetaPago) metodoPago).setCvv("123");
    }

    @Test
    void testCrearSuscripcion_Exitoso() {
        // Given
        when(suscripcionRepository.findByUsuarioIdAndEstado(usuario.getId(), EstadoSuscripcion.ACTIVA))
                .thenReturn(Optional.empty());
        when(suscripcionRepository.save(any(Suscripcion.class)))
                .thenAnswer(invocation -> {
                    Suscripcion s = invocation.getArgument(0);
                    s.setId(1L);
                    return s;
                });

        // When
        Suscripcion resultado = suscripcionService.crearSuscripcion(usuario, planBasic);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsuario()).isEqualTo(usuario);
        assertThat(resultado.getPlan()).isEqualTo(planBasic);
        assertThat(resultado.getEstado()).isEqualTo(EstadoSuscripcion.ACTIVA);
        assertThat(resultado.getFechaInicio()).isNotNull();
        assertThat(resultado.getFechaFin()).isNotNull();
        assertThat(resultado.getFechaFin()).isAfter(resultado.getFechaInicio());

        verify(suscripcionRepository, times(1)).save(any(Suscripcion.class));
        verify(facturaService, times(1)).generarFactura(any(Suscripcion.class), eq(planBasic.getPrecio()));
    }

    @Test
    void testCrearSuscripcion_YaTieneSuscripcionActiva() {
        // Given
        when(suscripcionRepository.findByUsuarioIdAndEstado(usuario.getId(), EstadoSuscripcion.ACTIVA))
                .thenReturn(Optional.of(suscripcionActiva));

        // When/Then
        assertThatThrownBy(() -> suscripcionService.crearSuscripcion(usuario, planBasic))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ya tiene una suscripción activa");

        verify(suscripcionRepository, never()).save(any(Suscripcion.class));
        verify(facturaService, never()).generarFactura(any(), any());
    }

    @Test
    void testCrearSuscripcionConMetodoPago_Exitoso() {
        // Given
        when(suscripcionRepository.findByUsuarioIdAndEstado(usuario.getId(), EstadoSuscripcion.ACTIVA))
                .thenReturn(Optional.empty());
        when(suscripcionRepository.save(any(Suscripcion.class)))
                .thenAnswer(invocation -> {
                    Suscripcion s = invocation.getArgument(0);
                    s.setId(1L);
                    return s;
                });

        // When
        Suscripcion resultado = suscripcionService.crearSuscripcionConMetodoPago(usuario, planBasic, metodoPago);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getMetodoPago()).isEqualTo(metodoPago);
        assertThat(resultado.getEstado()).isEqualTo(EstadoSuscripcion.ACTIVA);

        verify(suscripcionRepository, times(1)).save(any(Suscripcion.class));
        verify(facturaService, times(1)).generarFactura(any(Suscripcion.class), eq(planBasic.getPrecio()));
    }

    @Test
    void testCambiarPlan_Upgrade_ConProrrateo() {
        // Given
        when(suscripcionRepository.findByUsuarioIdAndEstado(usuario.getId(), EstadoSuscripcion.ACTIVA))
                .thenReturn(Optional.of(suscripcionActiva));
        when(suscripcionRepository.save(any(Suscripcion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Suscripcion resultado = suscripcionService.cambiarPlan(usuario.getId(), planPremium);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getPlan()).isEqualTo(planPremium);
        
        // Verificar que se generó factura de prorrateo
        verify(facturaService, times(1)).generarFactura(any(Suscripcion.class), any(BigDecimal.class));
        verify(suscripcionRepository, times(1)).save(any(Suscripcion.class));
    }

    @Test
    void testCambiarPlan_Downgrade_SinProrrateo() {
        // Given
        suscripcionActiva.setPlan(planPremium); // Ahora está en PREMIUM
        when(suscripcionRepository.findByUsuarioIdAndEstado(usuario.getId(), EstadoSuscripcion.ACTIVA))
                .thenReturn(Optional.of(suscripcionActiva));
        when(suscripcionRepository.save(any(Suscripcion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Suscripcion resultado = suscripcionService.cambiarPlan(usuario.getId(), planBasic);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getPlan()).isEqualTo(planBasic);
        
        // No debe generar factura de prorrateo en downgrade
        verify(facturaService, never()).generarFactura(any(Suscripcion.class), any(BigDecimal.class));
        verify(suscripcionRepository, times(1)).save(any(Suscripcion.class));
    }

    @Test
    void testCambiarPlan_MismoPlan() {
        // Given
        when(suscripcionRepository.findByUsuarioIdAndEstado(usuario.getId(), EstadoSuscripcion.ACTIVA))
                .thenReturn(Optional.of(suscripcionActiva));

        // When/Then
        assertThatThrownBy(() -> suscripcionService.cambiarPlan(usuario.getId(), planBasic))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya estás suscrito a este plan");

        verify(suscripcionRepository, never()).save(any(Suscripcion.class));
    }

    @Test
    void testCambiarPlan_SinSuscripcionActiva() {
        // Given
        when(suscripcionRepository.findByUsuarioIdAndEstado(usuario.getId(), EstadoSuscripcion.ACTIVA))
                .thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> suscripcionService.cambiarPlan(usuario.getId(), planPremium))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No hay suscripción activa");

        verify(suscripcionRepository, never()).save(any(Suscripcion.class));
    }

    @Test
    void testRenovarSuscripcion_Exitoso() {
        // Given
        LocalDateTime fechaFinOriginal = suscripcionActiva.getFechaFin();
        when(suscripcionRepository.findById(1L)).thenReturn(Optional.of(suscripcionActiva));
        when(suscripcionRepository.save(any(Suscripcion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Suscripcion resultado = suscripcionService.renovarSuscripcion(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getFechaFin()).isAfter(fechaFinOriginal);
        assertThat(resultado.getFechaFin()).isEqualTo(fechaFinOriginal.plusDays(30));

        verify(facturaService, times(1)).generarFactura(resultado, planBasic.getPrecio());
        verify(suscripcionRepository, times(1)).save(any(Suscripcion.class));
    }

    @Test
    void testRenovarSuscripcion_NoActiva() {
        // Given
        suscripcionActiva.setEstado(EstadoSuscripcion.CANCELADA);
        when(suscripcionRepository.findById(1L)).thenReturn(Optional.of(suscripcionActiva));

        // When/Then
        assertThatThrownBy(() -> suscripcionService.renovarSuscripcion(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no está activa");

        verify(suscripcionRepository, never()).save(any(Suscripcion.class));
        verify(facturaService, never()).generarFactura(any(), any());
    }

    @Test
    void testRenovarSuscripcion_NoEncontrada() {
        // Given
        when(suscripcionRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> suscripcionService.renovarSuscripcion(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no encontrada");
    }

    @Test
    void testCancelarSuscripcion_Exitoso() {
        // Given
        when(suscripcionRepository.findById(1L)).thenReturn(Optional.of(suscripcionActiva));
        when(suscripcionRepository.save(any(Suscripcion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Suscripcion resultado = suscripcionService.cancelarSuscripcion(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo(EstadoSuscripcion.CANCELADA);
        verify(suscripcionRepository, times(1)).save(any(Suscripcion.class));
    }

    @Test
    void testMarcarComoMorosa_Exitoso() {
        // Given
        when(suscripcionRepository.findById(1L)).thenReturn(Optional.of(suscripcionActiva));
        when(suscripcionRepository.save(any(Suscripcion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Suscripcion resultado = suscripcionService.marcarComoMorosa(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo(EstadoSuscripcion.MOROSA);
        verify(suscripcionRepository, times(1)).save(any(Suscripcion.class));
    }

    @Test
    void testObtenerSuscripcionActiva_Encontrada() {
        // Given
        when(suscripcionRepository.findByUsuarioIdAndEstado(usuario.getId(), EstadoSuscripcion.ACTIVA))
                .thenReturn(Optional.of(suscripcionActiva));

        // When
        Optional<Suscripcion> resultado = suscripcionService.obtenerSuscripcionActiva(usuario.getId());

        // Then
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEstado()).isEqualTo(EstadoSuscripcion.ACTIVA);
    }

    @Test
    void testObtenerSuscripcionActiva_NoEncontrada() {
        // Given
        when(suscripcionRepository.findByUsuarioIdAndEstado(usuario.getId(), EstadoSuscripcion.ACTIVA))
                .thenReturn(Optional.empty());

        // When
        Optional<Suscripcion> resultado = suscripcionService.obtenerSuscripcionActiva(usuario.getId());

        // Then
        assertThat(resultado).isEmpty();
    }

    @Test
    void testObtenerPorEstado() {
        // Given
        Suscripcion otraSuscripcion = new Suscripcion();
        otraSuscripcion.setId(2L);
        otraSuscripcion.setEstado(EstadoSuscripcion.ACTIVA);

        when(suscripcionRepository.findByEstado(EstadoSuscripcion.ACTIVA))
                .thenReturn(Arrays.asList(suscripcionActiva, otraSuscripcion));

        // When
        List<Suscripcion> resultado = suscripcionService.obtenerPorEstado(EstadoSuscripcion.ACTIVA);

        // Then
        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(s -> s.getEstado() == EstadoSuscripcion.ACTIVA);
    }

    @Test
    void testObtenerSuscripcionesParaRenovar() {
        // Given
        LocalDateTime ahora = LocalDateTime.now();
        when(suscripcionRepository.findSuscripcionesParaRenovar(ahora, EstadoSuscripcion.ACTIVA))
                .thenReturn(Arrays.asList(suscripcionActiva));

        // When
        List<Suscripcion> resultado = suscripcionService.obtenerSuscripcionesParaRenovar();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0)).isEqualTo(suscripcionActiva);
    }
}
