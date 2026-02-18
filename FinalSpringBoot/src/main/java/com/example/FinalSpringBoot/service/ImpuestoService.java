package com.example.FinalSpringBoot.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImpuestoService {

    private static final Map<String, BigDecimal> IMPUESTOS_POR_PAIS = new HashMap<>();

    static {
        // Europa
        IMPUESTOS_POR_PAIS.put("España", new BigDecimal("0.21"));      
        IMPUESTOS_POR_PAIS.put("Francia", new BigDecimal("0.20"));     
        IMPUESTOS_POR_PAIS.put("Alemania", new BigDecimal("0.19"));    
        IMPUESTOS_POR_PAIS.put("Italia", new BigDecimal("0.22"));     
        IMPUESTOS_POR_PAIS.put("Portugal", new BigDecimal("0.23"));    
        
        // América 
        IMPUESTOS_POR_PAIS.put("Argentina", new BigDecimal("0.21"));   
        IMPUESTOS_POR_PAIS.put("México", new BigDecimal("0.16"));      
        IMPUESTOS_POR_PAIS.put("Colombia", new BigDecimal("0.19"));    
        IMPUESTOS_POR_PAIS.put("Chile", new BigDecimal("0.19"));       
        IMPUESTOS_POR_PAIS.put("Estados Unidos", new BigDecimal("0.07")); 
        IMPUESTOS_POR_PAIS.put("Canadá", new BigDecimal("0.13"));      
        
        // Default
        IMPUESTOS_POR_PAIS.put("DEFAULT", new BigDecimal("0.15"));     
    }

    /**
     * Calcula el porcentaje de impuesto según el país
     * @param pais País del usuario
     * @return Porcentaje de impuesto (ej: 0.21 para 21%)
     */
    public BigDecimal obtenerPorcentajeImpuesto(String pais) {
        return IMPUESTOS_POR_PAIS.getOrDefault(pais, IMPUESTOS_POR_PAIS.get("DEFAULT"));
    }

    /**
     * Calcula el monto de impuesto a partir de un monto base
     * @param montoBruto Monto sin impuestos
     * @param pais País del usuario
     * @return Monto del impuesto
     */
    public BigDecimal calcularImpuesto(BigDecimal montoBruto, String pais) {
        BigDecimal porcentaje = obtenerPorcentajeImpuesto(pais);
        return montoBruto.multiply(porcentaje).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el monto total (bruto + impuesto)
     * @param montoBruto Monto sin impuestos
     * @param pais País del usuario
     * @return Monto total con impuestos incluidos
     */
    public BigDecimal calcularMontoTotal(BigDecimal montoBruto, String pais) {
        BigDecimal impuesto = calcularImpuesto(montoBruto, pais);
        return montoBruto.add(impuesto).setScale(2, RoundingMode.HALF_UP);
    }

    
    public Map<String, BigDecimal> obtenerTodosLosImpuestos() {
        return new HashMap<>(IMPUESTOS_POR_PAIS);
    }
}
