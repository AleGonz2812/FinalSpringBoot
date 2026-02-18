package com.example.FinalSpringBoot.repository;

import com.example.FinalSpringBoot.enums.TipoMetodoPago;
import com.example.FinalSpringBoot.model.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {

    List<MetodoPago> findByTipo(TipoMetodoPago tipo);

    List<MetodoPago> findByTitular(String titular);
}
