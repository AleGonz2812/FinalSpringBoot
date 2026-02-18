package com.example.FinalSpringBoot.service;

import com.example.FinalSpringBoot.enums.EstadoSuscripcion;
import com.example.FinalSpringBoot.enums.Rol;
import com.example.FinalSpringBoot.enums.TipoPlan;
import com.example.FinalSpringBoot.model.*;
import com.example.FinalSpringBoot.repository.FacturaRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class FacturaServiceTest {

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private ImpuestoService impuestoService;

    @InjectMocks
    private FacturaService facturaService;

    private Usuario usuario;
    private Plan plan;
    private Suscripcion suscripcion;
    private Factura factura;

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

        // Plan test
        plan = new Plan();
        plan.setId(1L);
        plan.setNombre(TipoPlan.BASIC);
        plan.setPrecio(new BigDecimal("9.99"));

        // Suscripción test
        suscripcion = new Suscripcion();
        suscripcion.setId(1L);
        suscripcion.setUsuario(usuario);
        suscripcion.setPlan(plan);
        suscripcion.setEstado(EstadoSuscripcion.ACTIVA);
        suscripcion.setFechaInicio(LocalDateTime.now().minusDays(1));
        suscripcion.setFechaFin(LocalDateTime.now().plusDays(29));

        // Factura test
        factura = new Factura();
        factura.setId(1L);
        factura.setFechaEmision(LocalDateTime.now());
        factura.setMontoBruto(new BigDecimal("9.99"));
        factura.setImpuesto(new BigDecimal("2.10"));
        factura.setMontoTotal(new BigDecimal("12.09"));
        factura.setSuscripcion(suscripcion);
    }

    @Test
    void testGenerarFactura_Exitoso() {
        // Given
        BigDecimal montoBruto = new BigDecimal("9.99");
        BigDecimal impuesto = new BigDecimal("2.10");

        when(impuestoService.calcularImpuesto(montoBruto, "España")).thenReturn(impuesto);
        when(facturaRepository.save(any(Factura.class))).thenAnswer(invocation -> {
            Factura f = invocation.getArgument(0);
            f.setId(1L);
            return f;
        });

        // When
        Factura resultado = facturaService.generarFactura(suscripcion, montoBruto);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getMontoBruto()).isEqualByComparingTo(montoBruto);
        assertThat(resultado.getImpuesto()).isEqualByComparingTo(impuesto);
        assertThat(resultado.getMontoTotal()).isEqualByComparingTo(new BigDecimal("12.09"));
        assertThat(resultado.getSuscripcion()).isEqualTo(suscripcion);
        assertThat(resultado.getFechaEmision()).isNotNull();

        verify(impuestoService, times(1)).calcularImpuesto(montoBruto, "España");
        verify(facturaRepository, times(1)).save(any(Factura.class));
    }

    @Test
    void testGenerarFactura_PaisDiferente() {
        // Given
        usuario.getPerfil().setPais("México");
        BigDecimal montoBruto = new BigDecimal("10.00");
        BigDecimal impuesto = new BigDecimal("1.60"); // 16% México

        when(impuestoService.calcularImpuesto(montoBruto, "México")).thenReturn(impuesto);
        when(facturaRepository.save(any(Factura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Factura resultado = facturaService.generarFactura(suscripcion, montoBruto);

        // Then
        assertThat(resultado.getImpuesto()).isEqualByComparingTo(impuesto);
        assertThat(resultado.getMontoTotal()).isEqualByComparingTo(new BigDecimal("11.60"));
        
        verify(impuestoService, times(1)).calcularImpuesto(montoBruto, "México");
    }

    @Test
    void testObtenerFacturasPorUsuario() {
        // Given
        Factura factura2 = new Factura();
        factura2.setId(2L);
        factura2.setFechaEmision(LocalDateTime.now().minusDays(30));
        factura2.setMontoBruto(new BigDecimal("9.99"));
        factura2.setImpuesto(new BigDecimal("2.10"));
        factura2.setMontoTotal(new BigDecimal("12.09"));

        when(facturaRepository.findByUsuarioIdOrderByFechaEmisionDesc(1L))
                .thenReturn(Arrays.asList(factura, factura2));

        // When
        List<Factura> resultado = facturaService.obtenerFacturasPorUsuario(1L);

        // Then
        assertThat(resultado).hasSize(2);
        assertThat(resultado).containsExactly(factura, factura2);
        verify(facturaRepository, times(1)).findByUsuarioIdOrderByFechaEmisionDesc(1L);
    }

    @Test
    void testObtenerFacturasPorSuscripcion() {
        // Given
        when(facturaRepository.findBySuscripcionId(1L)).thenReturn(Arrays.asList(factura));

        // When
        List<Factura> resultado = facturaService.obtenerFacturasPorSuscripcion(1L);

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0)).isEqualTo(factura);
    }

    @Test
    void testCalcularTotalFacturadoUsuario() {
        // Given
        Factura factura2 = new Factura();
        factura2.setId(2L);
        factura2.setMontoTotal(new BigDecimal("12.09"));

        Factura factura3 = new Factura();
        factura3.setId(3L);
        factura3.setMontoTotal(new BigDecimal("24.18"));

        when(facturaRepository.findByUsuarioIdOrderByFechaEmisionDesc(1L))
                .thenReturn(Arrays.asList(factura, factura2, factura3));

        // When
        BigDecimal total = facturaService.calcularTotalFacturadoUsuario(1L);

        // Then
        // 12.09 + 12.09 + 24.18 = 48.36
        assertThat(total).isEqualByComparingTo(new BigDecimal("48.36"));
    }

    @Test
    void testCalcularTotalFacturadoUsuario_SinFacturas() {
        // Given
        when(facturaRepository.findByUsuarioIdOrderByFechaEmisionDesc(1L))
                .thenReturn(Arrays.asList());

        // When
        BigDecimal total = facturaService.calcularTotalFacturadoUsuario(1L);

        // Then
        assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testFiltrarPorFechas() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fin = LocalDateTime.now();

        when(facturaRepository.findByFechaEmisionBetween(inicio, fin))
                .thenReturn(Arrays.asList(factura));

        // When
        List<Factura> resultado = facturaService.filtrarPorFechas(inicio, fin);

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0)).isEqualTo(factura);
    }

    @Test
    void testFiltrarPorMontoMayorA() {
        // Given
        BigDecimal monto = new BigDecimal("10.00");
        when(facturaRepository.findByMontoTotalGreaterThan(monto))
                .thenReturn(Arrays.asList(factura));

        // When
        List<Factura> resultado = facturaService.filtrarPorMontoMayorA(monto);

        // Then
        assertThat(resultado).hasSize(1);
        verify(facturaRepository, times(1)).findByMontoTotalGreaterThan(monto);
    }

    @Test
    void testFiltrarPorMontoMenorA() {
        // Given
        BigDecimal monto = new BigDecimal("20.00");
        when(facturaRepository.findByMontoTotalLessThan(monto))
                .thenReturn(Arrays.asList(factura));

        // When
        List<Factura> resultado = facturaService.filtrarPorMontoMenorA(monto);

        // Then
        assertThat(resultado).hasSize(1);
    }

    @Test
    void testBuscarConFiltros() {
        // Given
        LocalDateTime fechaInicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fechaFin = LocalDateTime.now();
        BigDecimal montoMin = new BigDecimal("10.00");
        BigDecimal montoMax = new BigDecimal("50.00");

        when(facturaRepository.findByFechaYMonto(fechaInicio, fechaFin, montoMin, montoMax))
                .thenReturn(Arrays.asList(factura));

        // When
        List<Factura> resultado = facturaService.buscarConFiltros(fechaInicio, fechaFin, montoMin, montoMax);

        // Then
        assertThat(resultado).hasSize(1);
        verify(facturaRepository, times(1)).findByFechaYMonto(fechaInicio, fechaFin, montoMin, montoMax);
    }

    @Test
    void testObtenerFacturaPorId_Encontrada() {
        // Given
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));

        // When
        Optional<Factura> resultado = facturaService.obtenerFacturaPorId(1L);

        // Then
        assertThat(resultado).isPresent();
        assertThat(resultado.get()).isEqualTo(factura);
    }

    @Test
    void testObtenerFacturaPorId_NoEncontrada() {
        // Given
        when(facturaRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Factura> resultado = facturaService.obtenerFacturaPorId(999L);

        // Then
        assertThat(resultado).isEmpty();
    }

    @Test
    void testEliminarFactura() {
        // Given
        doNothing().when(facturaRepository).deleteById(1L);

        // When
        facturaService.eliminarFactura(1L);

        // Then
        verify(facturaRepository, times(1)).deleteById(1L);
    }

    @Test
    void testObtenerTodasLasFacturas() {
        // Given
        Factura factura2 = new Factura();
        factura2.setId(2L);

        when(facturaRepository.findAll()).thenReturn(Arrays.asList(factura, factura2));

        // When
        List<Factura> resultado = facturaService.obtenerTodasLasFacturas();

        // Then
        assertThat(resultado).hasSize(2);
    }
}
