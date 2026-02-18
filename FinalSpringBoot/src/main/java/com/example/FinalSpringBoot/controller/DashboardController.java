package com.example.FinalSpringBoot.controller;

import com.example.FinalSpringBoot.model.Suscripcion;
import com.example.FinalSpringBoot.model.Usuario;
import com.example.FinalSpringBoot.service.FacturaService;
import com.example.FinalSpringBoot.service.SuscripcionService;
import com.example.FinalSpringBoot.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UsuarioService usuarioService;
    private final SuscripcionService suscripcionService;
    private final FacturaService facturaService;

     
    @GetMapping
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Optional<Suscripcion> suscripcionActiva = suscripcionService.obtenerSuscripcionActiva(usuario.getId());
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("perfil", usuario.getPerfil());
        model.addAttribute("suscripcionActiva", suscripcionActiva.orElse(null));
        model.addAttribute("facturas", facturaService.obtenerFacturasPorUsuario(usuario.getId()));
        model.addAttribute("totalFacturado", facturaService.calcularTotalFacturadoUsuario(usuario.getId()));

        return "dashboard";
    }

    
    @GetMapping("/perfil")
    public String perfil(Authentication authentication, Model model) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("usuario", usuario);
        model.addAttribute("perfil", usuario.getPerfil());

        return "perfil";
    }
}
