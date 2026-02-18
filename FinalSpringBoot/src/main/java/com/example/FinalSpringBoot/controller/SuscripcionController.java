package com.example.FinalSpringBoot.controller;

import com.example.FinalSpringBoot.enums.TipoMetodoPago;
import com.example.FinalSpringBoot.model.*;
import com.example.FinalSpringBoot.repository.MetodoPagoRepository;
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
    private final MetodoPagoRepository metodoPagoRepository;

    
    @GetMapping("/planes")
    public String verPlanes(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Restringir acceso a administradores
        if (usuario.getRol().name().equals("ADMIN")) {
            redirectAttributes.addFlashAttribute("error", "Los administradores no pueden suscribirse a planes");
            return "redirect:/admin";
        }

        Optional<Suscripcion> suscripcionActiva = suscripcionService.obtenerSuscripcionActiva(usuario.getId());

        model.addAttribute("planes", planService.obtenerTodosLosPlanes());
        model.addAttribute("suscripcionActiva", suscripcionActiva.orElse(null));
        
        return "suscripcion/planes";
    }

    
    @PostMapping("/crear")
    public String crearSuscripcion(
            @RequestParam Long planId,
            @RequestParam TipoMetodoPago tipoMetodoPago,
            @RequestParam(required = false) String numeroTarjeta,
            @RequestParam(required = false) String fechaVencimiento,
            @RequestParam(required = false) String cvv,
            @RequestParam(required = false) String titularTarjeta,
            @RequestParam(required = false) String emailPayPal,
            @RequestParam(required = false) String numeroCuenta,
            @RequestParam(required = false) String nombreBanco,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            String username = authentication.getName();
            Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Plan plan = planService.obtenerPlanPorId(planId)
                    .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

            // Crear el método de pago según el tipo
            MetodoPago metodoPago = null;
            switch (tipoMetodoPago) {
                case TARJETA:
                    TarjetaPago tarjeta = new TarjetaPago();
                    tarjeta.setTitular(titularTarjeta);
                    // Limpiar espacios del número de tarjeta
                    tarjeta.setNumeroTarjeta(numeroTarjeta != null ? numeroTarjeta.replaceAll("\\s+", "") : null);
                    tarjeta.setCvv(cvv != null ? cvv.replaceAll("\\s+", "") : null);
                    // Convertir MM/AA a YearMonth
                    if (fechaVencimiento != null && fechaVencimiento.contains("/")) {
                        String[] parts = fechaVencimiento.split("/");
                        int mes = Integer.parseInt(parts[0]);
                        int anio = 2000 + Integer.parseInt(parts[1]); // Asume 20XX
                        tarjeta.setFechaExpiracion(java.time.YearMonth.of(anio, mes));
                    }
                    tarjeta.setTipo(TipoMetodoPago.TARJETA);
                    metodoPago = tarjeta;
                    break;
                case PAYPAL:
                    PayPalPago paypal = new PayPalPago();
                    paypal.setTitular(emailPayPal); // Usar email como titular
                    paypal.setEmailPaypal(emailPayPal);
                    paypal.setTipo(TipoMetodoPago.PAYPAL);
                    metodoPago = paypal;
                    break;
                case TRANSFERENCIA:
                    TransferenciaPago transferencia = new TransferenciaPago();
                    transferencia.setTitular(nombreBanco); // Usar nombre del banco como titular
                    transferencia.setIban(numeroCuenta);
                    transferencia.setBanco(nombreBanco);
                    transferencia.setTipo(TipoMetodoPago.TRANSFERENCIA);
                    metodoPago = transferencia;
                    break;
            }

            // Guardar el método de pago
            @SuppressWarnings("null")
            MetodoPago metodoPagoGuardado = metodoPagoRepository.save(metodoPago);

            // Crear la suscripción con el método de pago
            suscripcionService.crearSuscripcionConMetodoPago(usuario, plan, metodoPagoGuardado);

            redirectAttributes.addFlashAttribute("mensaje", "Suscripción creada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/dashboard";
    }

   
    @PostMapping("/cambiar-plan")
    public String cambiarPlan(
            @RequestParam Long nuevoPlanId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            String username = authentication.getName();
            Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Plan nuevoPlan = planService.obtenerPlanPorId(nuevoPlanId)
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
