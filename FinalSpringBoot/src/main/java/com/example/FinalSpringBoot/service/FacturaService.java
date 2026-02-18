package com.example.FinalSpringBoot.service;

import com.example.FinalSpringBoot.model.Factura;
import com.example.FinalSpringBoot.model.Suscripcion;
import com.example.FinalSpringBoot.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final ImpuestoService impuestoService;

    public List<Factura> obtenerTodasLasFacturas() {
        return facturaRepository.findAll();
    }

    @SuppressWarnings("null")
    public Optional<Factura> obtenerFacturaPorId(Long id) {
        return facturaRepository.findById(id);
    }

    public List<Factura> obtenerFacturasPorUsuario(Long usuarioId) {
        return facturaRepository.findByUsuarioIdOrderByFechaEmisionDesc(usuarioId);
    }

    public List<Factura> obtenerFacturasPorSuscripcion(Long suscripcionId) {
        return facturaRepository.findBySuscripcionId(suscripcionId);
    }

    public List<Factura> filtrarPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return facturaRepository.findByFechaEmisionBetween(fechaInicio, fechaFin);
    }

    public List<Factura> filtrarPorMontoMayorA(BigDecimal monto) {
        return facturaRepository.findByMontoTotalGreaterThan(monto);
    }

    public List<Factura> filtrarPorMontoMenorA(BigDecimal monto) {
        return facturaRepository.findByMontoTotalLessThan(monto);
    }

    /**
     * Generar una factura para una suscripción
     * @param suscripcion Suscripción a facturar
     * @param montoBruto Monto sin impuestos (precio del plan o prorrateo)
     * @return Factura generada
     */
    @Transactional
    public Factura generarFactura(Suscripcion suscripcion, BigDecimal montoBruto) {
        String pais = suscripcion.getUsuario().getPerfil().getPais();
        
        BigDecimal impuesto = impuestoService.calcularImpuesto(montoBruto, pais);
        BigDecimal montoTotal = montoBruto.add(impuesto);
        
        Factura factura = new Factura();
        factura.setFechaEmision(LocalDateTime.now());
        factura.setMontoBruto(montoBruto);
        factura.setImpuesto(impuesto);
        factura.setMontoTotal(montoTotal);
        factura.setSuscripcion(suscripcion);
        
        return facturaRepository.save(factura);
    }

   
    public List<Factura> buscarConFiltros(LocalDateTime fechaInicio, LocalDateTime fechaFin, 
                                           BigDecimal montoMin, BigDecimal montoMax) {
        return facturaRepository.findByFechaYMonto(fechaInicio, fechaFin, montoMin, montoMax);
    }

  
    public BigDecimal calcularTotalFacturadoUsuario(Long usuarioId) {
        List<Factura> facturas = obtenerFacturasPorUsuario(usuarioId);
        return facturas.stream()
                .map(Factura::getMontoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

   
    @Transactional
    @SuppressWarnings("null")
    public void eliminarFactura(Long facturaId) {
        facturaRepository.deleteById(facturaId);
    }
}
