package com.example.FinalSpringBoot.repository;

import com.example.FinalSpringBoot.enums.TipoPlan;
import com.example.FinalSpringBoot.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    Optional<Plan> findByNombre(TipoPlan nombre);
}
