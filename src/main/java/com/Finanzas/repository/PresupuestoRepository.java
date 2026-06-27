package com.Finanzas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Finanzas.model.Presupuesto;

@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Integer>{

	List<Presupuesto> findByUsuarioIdUsuarioOrderByIdPresupuestoDesc(Integer idUsuario);
	
	@Query("""
			select p
			from Presupuesto as p
			where
				p.usuario.idUsuario = :idUsuario
				and
				(:mes is null or :mes = '' or p.mes = :mes)
				and
				(:anio is null or p.anio = :anio)
			order by p.idPresupuesto desc
			""")
	List<Presupuesto> findAllByFilters(
		@Param("idUsuario") Integer idUsuario,
		@Param("mes") String mes,
		@Param("anio") Integer anio
	);
	
	@Query("""
		    select p
		    from Presupuesto p
		    where p.idPresupuesto = :id
		      and p.usuario.idUsuario = :idUsuario
		""")
		Presupuesto findByIdPresupuestoAndIdUsuario(
		    @Param("id") Integer id,
		    @Param("idUsuario") Integer idUsuario
		);

	boolean existsByUsuarioIdUsuarioAndMesAndAnio(Integer idUsuario, String mes, Integer anio);
	
	boolean existsByUsuarioIdUsuarioAndMesAndAnioAndIdPresupuestoNot(Integer idUsuario, String mes, Integer anio, Integer idPresupuesto);
}
