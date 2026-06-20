package com.Finanzas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Finanzas.model.PresupuestoCategoria;
import com.Finanzas.repository.PresupuestoCategoriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PresupuestoCategoriaService {

	private final PresupuestoCategoriaRepository presupuestoCategoriaRepository;
	
	public List<PresupuestoCategoria> getAll() {
		return presupuestoCategoriaRepository.findAll();
	}
}
