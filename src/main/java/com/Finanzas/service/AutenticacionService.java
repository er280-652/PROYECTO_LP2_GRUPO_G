package com.Finanzas.service;

import org.springframework.stereotype.Service;

import com.Finanzas.dto.AutenticacionFilter;
import com.Finanzas.model.Usuario;
import com.Finanzas.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutenticacionService {

	private final UsuarioRepository usuarioRepository;
	
	public Usuario autenticathe(AutenticacionFilter filter) {
		return usuarioRepository.findByCuentaAndClave(filter.getCuenta(), filter.getClave());
	}
}
