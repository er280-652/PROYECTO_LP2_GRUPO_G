package com.Finanzas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Finanzas.model.Presupuesto;
import com.Finanzas.repository.PresupuestoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PresupuestoService {

	private final PresupuestoRepository presupuestoRepository;
	
	public List<Presupuesto> getAll() {
		return presupuestoRepository.findAll();
	}
}
