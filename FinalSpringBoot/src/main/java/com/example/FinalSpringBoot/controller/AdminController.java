package com.example.FinalSpringBoot.controller;

import com.example.FinalSpringBoot.enums.EstadoSuscripcion;
import com.example.FinalSpringBoot.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioService usuarioService;
    private final SuscripcionService suscripcionService;
    private final FacturaService facturaService;
    private final PlanService planService;
    private final RenovacionService renovacionService;

    // Panel de administración principal
     
    @GetMapping
    public String panelAdmin(Model model) {
        model.addAttribute("totalUsuarios", usuarioService.obtenerTodosLosUsuarios().size());
        model.addAttribute("suscripcionesActivas", suscripcionService.obtenerPorEstado(EstadoSuscripcion.ACTIVA).size());
        model.addAttribute("suscripcionesCanceladas", suscripcionService.obtenerPorEstado(EstadoSuscripcion.CANCELADA).size());
        model.addAttribute("suscripcionesMorosas", suscripcionService.obtenerPorEstado(EstadoSuscripcion.MOROSA).size());
        model.addAttribute("totalFacturas", facturaService.obtenerTodasLasFacturas().size());
        
        return "admin/dashboard";
    }

    // Ver todos los usuarios
    
    @GetMapping("/usuarios")
    public String verUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.obtenerTodosLosUsuarios());
        return "admin/usuarios";
    }

    //Ver detalles de un usuario específico (con historial de auditoría)
     
    @GetMapping("/usuarios/{id}")
    public String verDetalleUsuario(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.obtenerUsuarioPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")));
        model.addAttribute("suscripciones", suscripcionService.obtenerSuscripcionesPorUsuario(id));
        model.addAttribute("facturas", facturaService.obtenerFacturasPorUsuario(id));
        
        return "admin/usuario-detalle";
    }

    // Ver todas las suscripciones
     
    @GetMapping("/suscripciones")
    public String verSuscripciones(@RequestParam(required = false) String estado, Model model) {
        if (estado != null && !estado.isEmpty()) {
            model.addAttribute("suscripciones", suscripcionService.obtenerPorEstado(EstadoSuscripcion.valueOf(estado)));
        } else {
            model.addAttribute("suscripciones", suscripcionService.obtenerTodasLasSuscripciones());
        }
        return "admin/suscripciones";
    }

    // Ver todas las facturas
     
    @GetMapping("/facturas")
    public String verFacturas(Model model) {
        model.addAttribute("facturas", facturaService.obtenerTodasLasFacturas());
        return "admin/facturas";
    }

    // Gestión de planes
    
    @GetMapping("/planes")
    public String gestionarPlanes(Model model) {
        model.addAttribute("planes", planService.obtenerTodosLosPlanes());
        return "admin/planes";
    }

    // Eliminar usuario (y todas sus suscripciones y facturas)
    
    @PostMapping("/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminarUsuario(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // Forzar renovación de suscripciones (para testing)
    
    @PostMapping("/renovar-forzado")
    public String forzarRenovaciones(RedirectAttributes redirectAttributes) {
        try {
            renovacionService.forzarRenovaciones();
            redirectAttributes.addFlashAttribute("mensaje", "Proceso de renovación ejecutado manualmente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "info");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al ejecutar renovaciones: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    // Cancelar suscripción (admin puede cancelar suscripciones activas)
    
    @PostMapping("/suscripciones/{id}/cancelar")
    public String cancelarSuscripcion(@PathVariable Long id, @RequestParam(required = false) String estado, RedirectAttributes redirectAttributes) {
        try {
            suscripcionService.cancelarSuscripcion(id);
            redirectAttributes.addFlashAttribute("mensaje", "Suscripción cancelada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/suscripciones" + (estado != null ? "?estado=" + estado : "");
    }

    // Eliminar suscripción permanentemente (solo canceladas/morosas)
    
    @PostMapping("/suscripciones/{id}/eliminar")
    public String eliminarSuscripcion(@PathVariable Long id, @RequestParam(required = false) String estado, RedirectAttributes redirectAttributes) {
        try {
            suscripcionService.eliminarSuscripcion(id);
            redirectAttributes.addFlashAttribute("mensaje", "Suscripción eliminada del historial");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/suscripciones" + (estado != null ? "?estado=" + estado : "");
    }

    // Reactivar suscripción morosa (admin simula que se solucionó el problema de pago)
    
    @PostMapping("/suscripciones/{id}/reactivar")
    public String reactivarSuscripcion(@PathVariable Long id, @RequestParam(required = false) String estado, RedirectAttributes redirectAttributes) {
        try {
            suscripcionService.reactivarSuscripcion(id);
            redirectAttributes.addFlashAttribute("mensaje", "Suscripción reactivada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/suscripciones" + (estado != null ? "?estado=" + estado : "");
    }

    // Reintentar renovación de suscripción morosa
    
    @PostMapping("/suscripciones/{id}/reintentar")
    public String reintentarRenovacion(@PathVariable Long id, @RequestParam(required = false) String estado, RedirectAttributes redirectAttributes) {
        try {
            suscripcionService.renovarSuscripcion(id);
            redirectAttributes.addFlashAttribute("mensaje", "Renovación reintentada. Si el cobro fue exitoso, la suscripción está activa.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "info");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "El reintento falló: " + e.getMessage());
        }
        return "redirect:/admin/suscripciones" + (estado != null ? "?estado=" + estado : "");
    }

    // Historial de auditoría (Envers)
    @GetMapping("/auditoria")
    public String verAuditoria(Model model) {
        model.addAttribute("suscripciones", suscripcionService.obtenerTodasLasSuscripciones());
        return "admin/auditoria";
    }
}
