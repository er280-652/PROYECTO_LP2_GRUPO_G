package com.Finanzas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Finanzas.model.Movimiento;
import com.Finanzas.repository.MovimientoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovimientoService {

	private final MovimientoRepository movimientoRepository;
	
	public List<Movimiento> getAll() {
		return movimientoRepository.findAll();
	}
}
