package com.example.FinalSpringBoot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ImpuestoServiceTest {

    @InjectMocks
    private ImpuestoService impuestoService;

    @Test
    void testObtenerPorcentajeImpuesto_España() {
        // When
        BigDecimal porcentaje = impuestoService.obtenerPorcentajeImpuesto("España");

        // Then
        assertThat(porcentaje).isEqualByComparingTo(new BigDecimal("0.21"));
    }

    @Test
    void testObtenerPorcentajeImpuesto_Mexico() {
        // When
        BigDecimal porcentaje = impuestoService.obtenerPorcentajeImpuesto("México");

        // Then
        assertThat(porcentaje).isEqualByComparingTo(new BigDecimal("0.16"));
    }

    @Test
    void testObtenerPorcentajeImpuesto_Francia() {
        // When
        BigDecimal porcentaje = impuestoService.obtenerPorcentajeImpuesto("Francia");

        // Then
        assertThat(porcentaje).isEqualByComparingTo(new BigDecimal("0.20"));
    }

    @Test
    void testObtenerPorcentajeImpuesto_Argentina() {
        // When
        BigDecimal porcentaje = impuestoService.obtenerPorcentajeImpuesto("Argentina");

        // Then
        assertThat(porcentaje).isEqualByComparingTo(new BigDecimal("0.21"));
    }

    @Test
    void testObtenerPorcentajeImpuesto_EstadosUnidos() {
        // When
        BigDecimal porcentaje = impuestoService.obtenerPorcentajeImpuesto("Estados Unidos");

        // Then
        assertThat(porcentaje).isEqualByComparingTo(new BigDecimal("0.07"));
    }

    @Test
    void testObtenerPorcentajeImpuesto_PaisNoConfigurado() {
        // When
        BigDecimal porcentaje = impuestoService.obtenerPorcentajeImpuesto("PaisDesconocido");

        // Then
        // Debe devolver el impuesto por defecto (15%)
        assertThat(porcentaje).isEqualByComparingTo(new BigDecimal("0.15"));
    }

    @Test
    void testCalcularImpuesto_España() {
        // Given
        BigDecimal montoBruto = new BigDecimal("100.00");

        // When
        BigDecimal impuesto = impuestoService.calcularImpuesto(montoBruto, "España");

        // Then
        assertThat(impuesto).isEqualByComparingTo(new BigDecimal("21.00"));
    }

    @Test
    void testCalcularImpuesto_Mexico() {
        // Given
        BigDecimal montoBruto = new BigDecimal("100.00");

        // When
        BigDecimal impuesto = impuestoService.calcularImpuesto(montoBruto, "México");

        // Then
        assertThat(impuesto).isEqualByComparingTo(new BigDecimal("16.00"));
    }

    @Test
    void testCalcularImpuesto_ConDecimales() {
        // Given
        BigDecimal montoBruto = new BigDecimal("9.99");

        // When
        BigDecimal impuesto = impuestoService.calcularImpuesto(montoBruto, "España");

        // Then
        BigDecimal expected = new BigDecimal("9.99")
                .multiply(new BigDecimal("0.21"))
                .setScale(2, RoundingMode.HALF_UP);
        assertThat(impuesto).isEqualByComparingTo(expected);
    }

    @Test
    void testCalcularMontoTotal_España() {
        // Given
        BigDecimal montoBruto = new BigDecimal("100.00");

        // When
        BigDecimal montoTotal = impuestoService.calcularMontoTotal(montoBruto, "España");

        // Then
        assertThat(montoTotal).isEqualByComparingTo(new BigDecimal("121.00"));
    }

    @Test
    void testCalcularMontoTotal_Mexico() {
        // Given
        BigDecimal montoBruto = new BigDecimal("100.00");

        // When
        BigDecimal montoTotal = impuestoService.calcularMontoTotal(montoBruto, "México");

        // Then
        assertThat(montoTotal).isEqualByComparingTo(new BigDecimal("116.00"));
    }

    @Test
    void testCalcularMontoTotal_ConDecimales() {
        // Given
        BigDecimal montoBruto = new BigDecimal("9.99");

        // When
        BigDecimal montoTotal = impuestoService.calcularMontoTotal(montoBruto, "España");

        // Then
        // 9.99 + (9.99 * 0.21) = 9.99 + 2.10 = 12.09
        BigDecimal expected = new BigDecimal("12.09");
        assertThat(montoTotal).isEqualByComparingTo(expected);
    }

    @Test
    void testCalcularMontoTotal_PaisNoConfigurado() {
        // Given
        BigDecimal montoBruto = new BigDecimal("100.00");

        // When
        BigDecimal montoTotal = impuestoService.calcularMontoTotal(montoBruto, "PaisDesconocido");

        // Then
        // Debe usar el impuesto por defecto (15%)
        assertThat(montoTotal).isEqualByComparingTo(new BigDecimal("115.00"));
    }

    @Test
    void testObtenerTodosLosImpuestos() {
        // When
        Map<String, BigDecimal> impuestos = impuestoService.obtenerTodosLosImpuestos();

        // Then
        assertThat(impuestos).isNotNull();
        assertThat(impuestos).isNotEmpty();
        assertThat(impuestos).containsKey("España");
        assertThat(impuestos).containsKey("México");
        assertThat(impuestos).containsKey("Francia");
        assertThat(impuestos).containsKey("Argentina");
        assertThat(impuestos).containsKey("Estados Unidos");
        assertThat(impuestos).containsKey("DEFAULT");
    }

    @Test
    void testPrecision_RedondeoArriba() {
        // Given
        BigDecimal montoBruto = new BigDecimal("10.555");

        // When
        BigDecimal impuesto = impuestoService.calcularImpuesto(montoBruto, "España");

        // Then
        // 10.555 * 0.21 = 2.21655, redondeado a 2.22
        assertThat(impuesto).isEqualByComparingTo(new BigDecimal("2.22"));
    }

    @Test
    void testPrecision_RedondeoAbajo() {
        // Given
        BigDecimal montoBruto = new BigDecimal("10.544");

        // When
        BigDecimal impuesto = impuestoService.calcularImpuesto(montoBruto, "España");

        // Then
        // 10.544 * 0.21 = 2.21424, redondeado a 2.21
        assertThat(impuesto).isEqualByComparingTo(new BigDecimal("2.21"));
    }

    @Test
    void testCero_ImpuestoCero() {
        // Given
        BigDecimal montoBruto = BigDecimal.ZERO;

        // When
        BigDecimal impuesto = impuestoService.calcularImpuesto(montoBruto, "España");

        // Then
        assertThat(impuesto).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
