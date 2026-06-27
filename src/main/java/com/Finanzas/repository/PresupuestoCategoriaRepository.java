package com.Finanzas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Finanzas.model.PresupuestoCategoria;

@Repository
public interface PresupuestoCategoriaRepository extends JpaRepository<PresupuestoCategoria, Integer>{

	List<PresupuestoCategoria> findByPresupuestoIdPresupuestoOrderByIdPresupuestoCategoriaDesc(Integer idPresupuesto);
	
	@Query("""
		    select pc
		    from PresupuestoCategoria pc
		    where pc.idPresupuestoCategoria = :idPresupuestoCategoria
		      and pc.presupuesto.usuario.idUsuario = :idUsuario
		""")
		PresupuestoCategoria findByIdPresupuestoCategoriaAndIdUsuario(
		    @Param("idPresupuestoCategoria") Integer idPresupuestoCategoria,
		    @Param("idUsuario") Integer idUsuario
		);
	
	boolean existsByPresupuestoIdPresupuestoAndCategoriaIdCategoria(Integer idPresupuesto, Integer idCategoria);
	
	boolean existsByPresupuestoIdPresupuestoAndCategoriaIdCategoriaAndIdPresupuestoCategoriaNot(
			Integer idPresupuesto,
			Integer idCategoria,
			Integer idPresupuestoCategoria
	);
}
