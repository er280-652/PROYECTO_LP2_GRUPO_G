package com.Finanzas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Finanzas.model.Meta;

@Repository
public interface MetaRepository extends JpaRepository<Meta, Integer>{

	List<Meta> findByUsuarioIdUsuarioOrderByIdMetaDesc(Integer idUsuario);
	
	@Query("""
			select m
			from Meta m
			where m.idMeta = :idMeta
			  and m.usuario.idUsuario = :idUsuario
			""")
	Meta findByIdMetaAndIdUsuario(
			@Param("idMeta") Integer idMeta,
			@Param("idUsuario") Integer idUsuario
	);
	
	@Query("""
			select m
			from Meta m
			where
				m.usuario.idUsuario = :idUsuario
				and
				(
					:estado is null
					or :estado = ''
					or (:estado = 'completada' and m.completada = true)
					or (:estado = 'incompleta' and m.completada = false)
				)
			order by m.idMeta desc
			""")
	List<Meta> findAllByFilters(
			@Param("idUsuario") Integer idUsuario,
			@Param("estado") String estado
	);
}
