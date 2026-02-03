package com.example.saasfinal.service;

import com.example.saasfinal.model.entity.Plan;
import com.example.saasfinal.model.enums.TipoPlan;
import com.example.saasfinal.repository.PlanRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    /**
     * Inicializa los planes básicos si no existen
     */
    @PostConstruct
    @Transactional
    public void inicializarPlanes() {
        if (planRepository.count() == 0) {
            // Crear planes iniciales
            Plan basic = new Plan(TipoPlan.BASIC);
            Plan premium = new Plan(TipoPlan.PREMIUM);
            Plan enterprise = new Plan(TipoPlan.ENTERPRISE);

            planRepository.save(basic);
            planRepository.save(premium);
            planRepository.save(enterprise);

            System.out.println("✅ Planes inicializados correctamente");
        }
    }

    public List<Plan> obtenerTodosLosPlanes() {
        return planRepository.findAll();
    }

    public Plan obtenerPlanPorId(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
    }

    public Plan obtenerPlanPorTipo(TipoPlan tipo) {
        return planRepository.findByTipo(tipo)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
    }
}
