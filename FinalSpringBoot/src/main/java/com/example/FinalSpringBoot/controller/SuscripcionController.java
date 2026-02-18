package com.example.FinalSpringBoot.controller;

import com.example.FinalSpringBoot.enums.TipoPlan;
import com.example.FinalSpringBoot.model.Plan;
import com.example.FinalSpringBoot.model.Suscripcion;
import com.example.FinalSpringBoot.model.Usuario;
import com.example.FinalSpringBoot.service.PlanService;
import com.example.FinalSpringBoot.service.SuscripcionService;
import com.example.FinalSpringBoot.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/suscripcion")
@RequiredArgsConstructor
public class SuscripcionController {

    private final SuscripcionService suscripcionService;
    private final PlanService planService;
    private final UsuarioService usuarioService;

    
    @GetMapping("/planes")
    public String verPlanes(Authentication authentication, Model model) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Optional<Suscripcion> suscripcionActiva = suscripcionService.obtenerSuscripcionActiva(usuario.getId());

        model.addAttribute("planes", planService.obtenerTodosLosPlanes());
        model.addAttribute("suscripcionActiva", suscripcionActiva.orElse(null));
        
        return "planes";
    }

    
    @PostMapping("/crear")
    public String crearSuscripcion(
            @RequestParam String tipoPlan,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            String username = authentication.getName();
            Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Plan plan = planService.obtenerPlanPorTipo(TipoPlan.valueOf(tipoPlan))
                    .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

            suscripcionService.crearSuscripcion(usuario, plan);

            redirectAttributes.addFlashAttribute("mensaje", "Suscripción creada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/dashboard";
    }

   
    @PostMapping("/cambiar-plan")
    public String cambiarPlan(
            @RequestParam String tipoPlan,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            String username = authentication.getName();
            Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Plan nuevoPlan = planService.obtenerPlanPorTipo(TipoPlan.valueOf(tipoPlan))
                    .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

            Suscripcion suscripcion = suscripcionService.cambiarPlan(usuario.getId(), nuevoPlan);

            boolean esUpgrade = nuevoPlan.getPrecio().compareTo(suscripcion.getPlan().getPrecio()) > 0;
            
            if (esUpgrade) {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "Plan actualizado a " + nuevoPlan.getNombre() + ". Se ha generado una factura por el prorrateo.");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "Plan actualizado a " + nuevoPlan.getNombre() + ". El cambio se reflejará en tu próxima factura.");
            }
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/cancelar")
    public String cancelarSuscripcion(
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            String username = authentication.getName();
            Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Suscripcion suscripcion = suscripcionService.obtenerSuscripcionActiva(usuario.getId())
                    .orElseThrow(() -> new RuntimeException("No tienes una suscripción activa"));

            suscripcionService.cancelarSuscripcion(suscripcion.getId());

            redirectAttributes.addFlashAttribute("mensaje", "Suscripción cancelada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "info");
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/dashboard";
    }

    
    @GetMapping("/historial")
    public String verHistorial(Authentication authentication, Model model) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("suscripciones", suscripcionService.obtenerSuscripcionesPorUsuario(usuario.getId()));
        
        return "historial-suscripciones";
    }
}
