package com.example.FinalSpringBoot.service;

import com.example.FinalSpringBoot.enums.TipoPlan;
import com.example.FinalSpringBoot.model.Plan;
import com.example.FinalSpringBoot.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    public List<Plan> obtenerTodosLosPlanes() {
        return planRepository.findAll();
    }

    public Optional<Plan> obtenerPlanPorId(Long id) {
        return planRepository.findById(id);
    }

    public Optional<Plan> obtenerPlanPorTipo(TipoPlan tipo) {
        return planRepository.findByNombre(tipo);
    }

    @Transactional
    public Plan guardarPlan(Plan plan) {
        return planRepository.save(plan);
    }

    @Transactional
    public void inicializarPlanes() {
        if (planRepository.count() == 0) {
            Plan basic = new Plan();
            basic.setNombre(TipoPlan.BASIC);
            basic.setPrecio(new BigDecimal("9.99"));
            basic.setDescripcion("Plan básico con funcionalidades esenciales");

            Plan premium = new Plan();
            premium.setNombre(TipoPlan.PREMIUM);
            premium.setPrecio(new BigDecimal("29.99"));
            premium.setDescripcion("Plan premium con funcionalidades avanzadas");

            Plan enterprise = new Plan();
            enterprise.setNombre(TipoPlan.ENTERPRISE);
            enterprise.setPrecio(new BigDecimal("99.99"));
            enterprise.setDescripcion("Plan empresarial con todas las funcionalidades");

            planRepository.save(basic);
            planRepository.save(premium);
            planRepository.save(enterprise);
        }
    }
}
