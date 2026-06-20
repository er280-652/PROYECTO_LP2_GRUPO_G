package com.Finanzas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Finanzas.model.Meta;
import com.Finanzas.repository.MetaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetaService {

	private final MetaRepository metaRepository;
	
	public List<Meta> getAll() {
		return metaRepository.findAll();
	}
}
