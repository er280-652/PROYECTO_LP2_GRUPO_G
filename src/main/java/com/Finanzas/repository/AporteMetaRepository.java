package com.Finanzas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Finanzas.model.AporteMeta;

@Repository
public interface AporteMetaRepository extends JpaRepository<AporteMeta, Integer>{

	List<AporteMeta> findByMetaIdMetaOrderByIdAporteDesc(Integer idMeta);
	
	boolean existsByMovimientoIdMovimiento(Integer idMovimiento);
	
	@Query("""
			select a
			from AporteMeta a
			where a.idAporte = :idAporte
			  and a.meta.usuario.idUsuario = :idUsuario
			""")
	AporteMeta findByIdAporteAndIdUsuario(
			@Param("idAporte") Integer idAporte,
			@Param("idUsuario") Integer idUsuario
	);
}
