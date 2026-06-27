package com.Finanzas.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.Finanzas.dto.PresupuestoFilter;
import com.Finanzas.dto.ResultadoResponse;
import com.Finanzas.model.Presupuesto;
import com.Finanzas.model.Usuario;
import com.Finanzas.repository.PresupuestoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PresupuestoService {

	private final PresupuestoRepository presupuestoRepository;
	
	public List<Presupuesto> getAll() {
		return presupuestoRepository.findAll();
	}
	
	public List<Presupuesto> getByUsuarioActual(Integer idUsuario) {
		return presupuestoRepository.findByUsuarioIdUsuarioOrderByIdPresupuestoDesc(idUsuario);
	}
	
	public List<Presupuesto> search(PresupuestoFilter filter, Integer idUsuario) {
		return presupuestoRepository.findAllByFilters(idUsuario, filter.getMes(), filter.getAnio());
	}
	
	public Presupuesto getOne(Integer idPresupuesto, Integer idUsuario) {
		return presupuestoRepository.findByIdPresupuestoAndIdUsuario(idPresupuesto, idUsuario);
	}
	
	public ResultadoResponse create(Presupuesto presupuesto, Integer idUsuario) {
		try {
			var validacion = validar(presupuesto);
			if (!validacion.success()) {
				return validacion;
			}
			
			if (presupuestoRepository.existsByUsuarioIdUsuarioAndMesAndAnio(idUsuario, presupuesto.getMes(), presupuesto.getAnio())) {
				return new ResultadoResponse(false, "Ya existe un presupuesto registrado para ese mes y año");
			}
			
			Usuario usuario = new Usuario();
			usuario.setIdUsuario(idUsuario);
			
			presupuesto.setUsuario(usuario);
			
			var registro = presupuestoRepository.save(presupuesto);
			var mensaje = String.format("Presupuesto con ID %s registrado", registro.getIdPresupuesto());
			
			return new ResultadoResponse(true, mensaje);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultadoResponse(false, "Hubo un error en la transacción");
		}
	}
	
	public ResultadoResponse update(Presupuesto presupuesto, Integer idUsuario) {
		try {
			var validacion = validar(presupuesto);
			if (!validacion.success()) {
				return validacion;
			}
			
			var original = presupuestoRepository.findByIdPresupuestoAndIdUsuario(presupuesto.getIdPresupuesto(), idUsuario);
			
			if (original == null) {
				return new ResultadoResponse(false, "El presupuesto no existe o no pertenece al usuario actual");
			}
			
			if (presupuestoRepository.existsByUsuarioIdUsuarioAndMesAndAnioAndIdPresupuestoNot(
					idUsuario, presupuesto.getMes(), presupuesto.getAnio(), presupuesto.getIdPresupuesto())) {
				return new ResultadoResponse(false, "Ya existe otro presupuesto registrado para ese mes y año");
			}
			
			original.setMes(presupuesto.getMes());
			original.setAnio(presupuesto.getAnio());
			original.setMontoTotal(presupuesto.getMontoTotal());
			
			var registro = presupuestoRepository.save(original);
			var mensaje = String.format("Presupuesto con ID %s actualizado", registro.getIdPresupuesto());
			
			return new ResultadoResponse(true, mensaje);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultadoResponse(false, "Hubo un error en la transacción");
		}
	}
	
	public Integer getAnioActual() {
		return LocalDate.now().getYear();
	}
	
	private ResultadoResponse validar(Presupuesto presupuesto) {
		if (presupuesto.getMes() == null || presupuesto.getMes().isBlank()) {
			return new ResultadoResponse(false, "Debe seleccionar un mes");
		}
		
		if (presupuesto.getAnio() == null) {
			return new ResultadoResponse(false, "Debe ingresar un año");
		}
		
		if (presupuesto.getAnio() < 1900 || presupuesto.getAnio() > 9999) {
			return new ResultadoResponse(false, "El año debe tener 4 dígitos");
		}
		
		if (presupuesto.getMontoTotal() == null || presupuesto.getMontoTotal() <= 0) {
			return new ResultadoResponse(false, "El monto total debe ser mayor a 0");
		}
		
		return new ResultadoResponse(true, "Validación correcta");
	}
}
