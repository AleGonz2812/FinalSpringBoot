package com.example.FinalSpringBoot.controller;

import com.example.FinalSpringBoot.model.Usuario;
import com.example.FinalSpringBoot.service.FacturaService;
import com.example.FinalSpringBoot.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaService facturaService;
    private final UsuarioService usuarioService;

    
    @GetMapping
    public String verFacturas(Authentication authentication, Model model) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("facturas", facturaService.obtenerFacturasPorUsuario(usuario.getId()));
        model.addAttribute("totalFacturado", facturaService.calcularTotalFacturadoUsuario(usuario.getId()));
        
        return "facturas";
    }

    
    @GetMapping("/filtrar")
    public String filtrarFacturas(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) BigDecimal montoMin,
            @RequestParam(required = false) BigDecimal montoMax,
            Authentication authentication,
            Model model) {
        
        String username = authentication.getName();
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (fechaInicio == null && fechaFin == null && montoMin == null && montoMax == null) {
            model.addAttribute("facturas", facturaService.obtenerFacturasPorUsuario(usuario.getId()));
        } else {
            LocalDateTime inicio = fechaInicio != null ? LocalDateTime.parse(fechaInicio) : LocalDateTime.now().minusYears(10);
            LocalDateTime fin = fechaFin != null ? LocalDateTime.parse(fechaFin) : LocalDateTime.now();
            BigDecimal min = montoMin != null ? montoMin : BigDecimal.ZERO;
            BigDecimal max = montoMax != null ? montoMax : new BigDecimal("999999");
            
            model.addAttribute("facturas", facturaService.buscarConFiltros(inicio, fin, min, max));
        }
        
        model.addAttribute("totalFacturado", facturaService.calcularTotalFacturadoUsuario(usuario.getId()));
        return "facturas";
    }
}
