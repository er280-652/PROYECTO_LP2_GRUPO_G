package com.Finanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Finanzas.model.Categoria;
import com.Finanzas.model.Tipo;
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer>{

}
