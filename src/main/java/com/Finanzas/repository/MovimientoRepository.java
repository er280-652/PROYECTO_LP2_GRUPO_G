package com.Finanzas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Finanzas.model.Movimiento;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer>{

    List<Movimiento> findAllByOrderByIdMovimientoDesc();

    List<Movimiento> findByCategoriaTipoIgnoreCaseOrderByIdMovimientoDesc(String tipo);
    
    @Query("""
    		SELECT m
    		FROM Movimiento m
    		WHERE m.usuario.idUsuario = :idUsuario
    		ORDER BY m.idMovimiento DESC
    		""")
    		List<Movimiento> findByUsuario(@Param("idUsuario") Integer idUsuario);
}