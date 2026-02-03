package com.example.saasfinal.repository;

import com.example.saasfinal.model.entity.Plan;
import com.example.saasfinal.model.enums.TipoPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    
    Optional<Plan> findByTipo(TipoPlan tipo);
    
    boolean existsByTipo(TipoPlan tipo);
}
