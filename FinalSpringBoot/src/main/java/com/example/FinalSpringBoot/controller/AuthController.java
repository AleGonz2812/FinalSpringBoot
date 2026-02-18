package com.example.FinalSpringBoot.controller;

import com.example.FinalSpringBoot.service.PlanService;
import com.example.FinalSpringBoot.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final PlanService planService;

    @PostMapping("/registrar")
    public String registrarUsuario(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String pais,
            RedirectAttributes redirectAttributes) {
        
        try {
            usuarioService.registrarUsuario(username, email, password, nombre, apellido, pais);
            
            redirectAttributes.addFlashAttribute("mensaje", "Usuario registrado exitosamente. Por favor inicia sesión.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/login";
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro";
        }
    }

    // Mostrar página de selección de plan (después del registro)
     
    @GetMapping("/seleccionar-plan")
    public String seleccionarPlan(Model model) {
        model.addAttribute("planes", planService.obtenerTodosLosPlanes());
        return "seleccionar-plan";
    }
}
