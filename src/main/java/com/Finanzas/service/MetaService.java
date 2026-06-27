package com.Finanzas.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.Finanzas.dto.MetaFilter;
import com.Finanzas.dto.ResultadoResponse;
import com.Finanzas.model.Meta;
import com.Finanzas.model.Usuario;
import com.Finanzas.repository.MetaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetaService {

	private final MetaRepository metaRepository;
	
	public List<Meta> getAll() {
		return metaRepository.findAll();
	}
	
	public List<Meta> getByUsuario(Integer idUsuario) {
		return metaRepository.findByUsuarioIdUsuarioOrderByIdMetaDesc(idUsuario);
	}
	
	public List<Meta> search(MetaFilter filter, Integer idUsuario) {
		return metaRepository.findAllByFilters(idUsuario, filter.getEstado());
	}
	
	public Meta getOne(Integer idMeta, Integer idUsuario) {
		return metaRepository.findByIdMetaAndIdUsuario(idMeta, idUsuario);
	}
	
	public ResultadoResponse create(Meta meta, Integer idUsuario) {
		try {
			var validacion = validar(meta, true);
			
			if (!validacion.success()) {
				return validacion;
			}
			
			Usuario usuario = new Usuario();
			usuario.setIdUsuario(idUsuario);
			
			meta.setUsuario(usuario);
			meta.setCompletada(false);
			
			if (meta.getMontoActual() == null) {
				meta.setMontoActual(0.0);
			}
			
			var registro = metaRepository.save(meta);
			var mensaje = String.format("Meta con ID %s registrada", registro.getIdMeta());
			
			return new ResultadoResponse(true, mensaje);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultadoResponse(false, "Hubo un error en la transacción");
		}
	}
	
	public ResultadoResponse update(Meta meta, Integer idUsuario) {
		try {
			var original = metaRepository.findByIdMetaAndIdUsuario(meta.getIdMeta(), idUsuario);
			
			if (original == null) {
				return new ResultadoResponse(false, "La meta no existe o no pertenece al usuario actual");
			}
			
			var validacion = validar(meta, false);
			
			if (!validacion.success()) {
				return validacion;
			}
			
			original.setNombre(meta.getNombre());
			original.setDescripcion(meta.getDescripcion());
			original.setMontoObjetivo(meta.getMontoObjetivo());
			original.setMontoActual(meta.getMontoActual());
			original.setFechaObjetivo(meta.getFechaObjetivo());
			original.setCompletada(meta.getMontoActual() >= meta.getMontoObjetivo());
			
			var registro = metaRepository.save(original);
			var mensaje = String.format("Meta con ID %s actualizada", registro.getIdMeta());
			
			return new ResultadoResponse(true, mensaje);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultadoResponse(false, "Hubo un error en la transacción");
		}
	}
	
	private ResultadoResponse validar(Meta meta, boolean registroNuevo) {
		if (meta.getNombre() == null || meta.getNombre().isBlank()) {
			return new ResultadoResponse(false, "Debe ingresar el nombre de la meta");
		}
		
		if (meta.getMontoObjetivo() == null || meta.getMontoObjetivo() <= 0) {
			return new ResultadoResponse(false, "El monto objetivo debe ser mayor a 0");
		}
		
		if (meta.getMontoActual() == null) {
			meta.setMontoActual(0.0);
		}
		
		if (meta.getMontoActual() < 0) {
			return new ResultadoResponse(false, "El monto actual no puede ser negativo");
		}
		
		if (registroNuevo && meta.getMontoActual() >= meta.getMontoObjetivo()) {
			return new ResultadoResponse(false, "El monto actual debe ser menor al monto objetivo");
		}
		
		if (!registroNuevo && meta.getMontoActual() > meta.getMontoObjetivo()) {
			return new ResultadoResponse(false, "El monto actual no puede superar el monto objetivo");
		}
		
		if (meta.getFechaObjetivo() == null) {
			return new ResultadoResponse(false, "Debe ingresar la fecha límite");
		}
		
		if (meta.getFechaObjetivo().isBefore(LocalDate.now())) {
			return new ResultadoResponse(false, "La fecha límite no puede ser anterior a la fecha actual");
		}
		
		return new ResultadoResponse(true, "Validación correcta");
	}
}
