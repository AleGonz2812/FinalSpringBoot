package com.example.saasfinal.controller;

import com.example.saasfinal.model.entity.Plan;
import com.example.saasfinal.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Controller para gestionar y mostrar los planes disponibles
 * SEMANA 1: Vista funcional para validar que los planes se guardan correctamente
 */
@Controller
@RequestMapping("/planes")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @GetMapping
    public String listarPlanes(Model model) {
        List<Plan> planes = planService.obtenerTodosLosPlanes();
        model.addAttribute("planes", planes);
        return "planes/lista";
    }
}
