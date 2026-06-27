package com.Finanzas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Finanzas.model.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    List<Categoria> findByTipo(String tipo);
    
    List<Categoria> findByTipoIgnoreCaseAndActivoTrue(String tipo);
    
    Categoria findByDescripcionIgnoreCaseAndTipoIgnoreCaseAndActivoTrue(String descripcion, String tipo);
}