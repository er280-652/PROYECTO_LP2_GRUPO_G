package com.Finanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Finanzas.model.Tipo;
import com.Finanzas.model.Usuario;
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
	
	Usuario findByCuentaAndClave(String cuenta, String clave);

}
