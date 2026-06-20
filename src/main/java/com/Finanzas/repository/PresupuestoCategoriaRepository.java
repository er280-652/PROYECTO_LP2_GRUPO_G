package com.Finanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Finanzas.model.Presupuesto;
import com.Finanzas.model.PresupuestoCategoria;
import com.Finanzas.model.Tipo;
@Repository
public interface PresupuestoCategoriaRepository extends JpaRepository<PresupuestoCategoria, Integer>{

}
