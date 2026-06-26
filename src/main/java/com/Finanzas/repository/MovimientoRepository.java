package com.Finanzas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Finanzas.model.Movimiento;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer>{

    List<Movimiento> findAllByOrderByIdMovimientoDesc();

    List<Movimiento> findByCategoriaTipoOrderByIdMovimientoDesc(String tipo);
}