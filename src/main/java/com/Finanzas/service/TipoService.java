package com.Finanzas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Finanzas.model.Tipo;
import com.Finanzas.repository.TipoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoService {

	private final TipoRepository tiporepository;
	
	public List<Tipo> getAll() {
		return tiporepository.findAll();
	}
}
