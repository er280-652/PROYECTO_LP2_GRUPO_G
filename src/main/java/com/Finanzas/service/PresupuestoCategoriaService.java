package com.Finanzas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Finanzas.dto.ResultadoResponse;
import com.Finanzas.model.Categoria;
import com.Finanzas.model.Presupuesto;
import com.Finanzas.model.PresupuestoCategoria;
import com.Finanzas.repository.CategoriaRepository;
import com.Finanzas.repository.PresupuestoCategoriaRepository;
import com.Finanzas.repository.PresupuestoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PresupuestoCategoriaService {

	private final PresupuestoCategoriaRepository presupuestoCategoriaRepository;
	private final PresupuestoRepository presupuestoRepository;
	private final CategoriaRepository categoriaRepository;
	
	public List<PresupuestoCategoria> getAll() {
		return presupuestoCategoriaRepository.findAll();
	}
	
	public List<PresupuestoCategoria> getByPresupuesto(Integer idPresupuesto, Integer idUsuario) {
		var presupuesto = presupuestoRepository.findByIdPresupuestoAndIdUsuario(idPresupuesto, idUsuario);
		
		if (presupuesto == null) {
			return List.of();
		}
		
		return presupuestoCategoriaRepository.findByPresupuestoIdPresupuestoOrderByIdPresupuestoCategoriaDesc(idPresupuesto);
	}
	
	public PresupuestoCategoria getOne(Integer idPresupuestoCategoria, Integer idUsuario) {
		return presupuestoCategoriaRepository.findByIdPresupuestoCategoriaAndIdUsuario(idPresupuestoCategoria, idUsuario);
	}
	
	public Double getTotalAsignado(Integer idPresupuesto, Integer idUsuario) {
		var presupuesto = presupuestoRepository.findByIdPresupuestoAndIdUsuario(idPresupuesto, idUsuario);
		
		if (presupuesto == null) {
			return 0.0;
		}
		
		return calcularTotalAsignadoActivo(idPresupuesto, null);
	}
	
	public Double getSaldoDisponible(Integer idPresupuesto, Integer idUsuario) {
		var presupuesto = presupuestoRepository.findByIdPresupuestoAndIdUsuario(idPresupuesto, idUsuario);
		
		if (presupuesto == null) {
			return 0.0;
		}
		
		return presupuesto.getMontoTotal() - getTotalAsignado(idPresupuesto, idUsuario);
	}
	
	public ResultadoResponse create(PresupuestoCategoria presupuestoCategoria, Integer idPresupuesto, Integer idUsuario) {
		try {
			var presupuesto = presupuestoRepository.findByIdPresupuestoAndIdUsuario(idPresupuesto, idUsuario);
			
			if (presupuesto == null) {
				return new ResultadoResponse(false, "El presupuesto no existe o no pertenece al usuario actual");
			}
			
			presupuestoCategoria.setActivo(true);
			
			var validacion = validar(presupuestoCategoria, presupuesto, null, true);
			
			if (!validacion.success()) {
				return validacion;
			}
			
			Integer idCategoria = presupuestoCategoria.getCategoria().getIdCategoria();
			
			if (presupuestoCategoriaRepository.existsByPresupuestoIdPresupuestoAndCategoriaIdCategoria(idPresupuesto, idCategoria)) {
				return new ResultadoResponse(false, "La categoría ya fue asignada a este presupuesto");
			}
			
			Presupuesto presupuestoBD = new Presupuesto();
			presupuestoBD.setIdPresupuesto(idPresupuesto);
			
			Categoria categoriaBD = new Categoria();
			categoriaBD.setIdCategoria(idCategoria);
			
			presupuestoCategoria.setPresupuesto(presupuestoBD);
			presupuestoCategoria.setCategoria(categoriaBD);
			
			var registro = presupuestoCategoriaRepository.save(presupuestoCategoria);
			var mensaje = String.format("Categoría de presupuesto con ID %s registrada", registro.getIdPresupuestoCategoria());
			
			return new ResultadoResponse(true, mensaje);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultadoResponse(false, "Hubo un error en la transacción");
		}
	}
	
	public ResultadoResponse update(PresupuestoCategoria presupuestoCategoria, Integer idUsuario) {
		try {
			var original = presupuestoCategoriaRepository.findByIdPresupuestoCategoriaAndIdUsuario(
					presupuestoCategoria.getIdPresupuestoCategoria(),
					idUsuario
			);
			
			if (original == null) {
				return new ResultadoResponse(false, "El detalle de presupuesto no existe o no pertenece al usuario actual");
			}
			
			boolean activoParaPresupuesto = Boolean.TRUE.equals(original.getActivo());
			
			var validacion = validar(
					presupuestoCategoria, 
					original.getPresupuesto(), 
					original.getIdPresupuestoCategoria(), 
					activoParaPresupuesto
			);
			
			if (!validacion.success()) {
				return validacion;
			}
			
			Integer idPresupuesto = original.getPresupuesto().getIdPresupuesto();
			Integer idCategoria = presupuestoCategoria.getCategoria().getIdCategoria();
			
			if (presupuestoCategoriaRepository.existsByPresupuestoIdPresupuestoAndCategoriaIdCategoriaAndIdPresupuestoCategoriaNot(
					idPresupuesto,
					idCategoria,
					original.getIdPresupuestoCategoria())) {
				return new ResultadoResponse(false, "La categoría ya fue asignada a este presupuesto");
			}
			
			Categoria categoriaBD = new Categoria();
			categoriaBD.setIdCategoria(idCategoria);
			
			original.setCategoria(categoriaBD);
			original.setMontoAsignado(presupuestoCategoria.getMontoAsignado());
			
			var registro = presupuestoCategoriaRepository.save(original);
			var mensaje = String.format("Categoría de presupuesto con ID %s actualizada", registro.getIdPresupuestoCategoria());
			
			return new ResultadoResponse(true, mensaje);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultadoResponse(false, "Hubo un error en la transacción");
		}
	}
	
	@Transactional
	public ResultadoResponse changeActive(Integer idPresupuestoCategoria, Integer idUsuario) {
		var original = presupuestoCategoriaRepository.findByIdPresupuestoCategoriaAndIdUsuario(
				idPresupuestoCategoria,
				idUsuario
		);
		
		if (original == null) {
			return new ResultadoResponse(false, "El detalle de presupuesto no existe o no pertenece al usuario actual");
		}
		
		try {
			boolean nuevoEstado = !Boolean.TRUE.equals(original.getActivo());
			
			if (nuevoEstado) {
				var validacion = validarActivacion(original);
				
				if (!validacion.success()) {
					return validacion;
				}
			}
			
			original.setActivo(nuevoEstado);
			
			var estado = nuevoEstado ? "activada" : "desactivada";
			var mensaje = String.format(
					"Categoría de presupuesto con ID %s %s", 
					original.getIdPresupuestoCategoria(), 
					estado
			);
			
			return new ResultadoResponse(true, mensaje);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultadoResponse(false, "Hubo un error en la transacción");
		}
	}
	
	private ResultadoResponse validarActivacion(PresupuestoCategoria presupuestoCategoria) {
		var presupuesto = presupuestoCategoria.getPresupuesto();
		
		Double totalAsignado = calcularTotalAsignadoActivo(
				presupuesto.getIdPresupuesto(), 
				presupuestoCategoria.getIdPresupuestoCategoria()
		);
		
		Double nuevoTotal = totalAsignado + presupuestoCategoria.getMontoAsignado();
		
		if (nuevoTotal > presupuesto.getMontoTotal()) {
			return new ResultadoResponse(false, "No se puede activar porque la suma asignada superaría el presupuesto total");
		}
		
		return new ResultadoResponse(true, "Validación correcta");
	}
	
	private ResultadoResponse validar(
			PresupuestoCategoria presupuestoCategoria, 
			Presupuesto presupuesto, 
			Integer idDetalleActual, 
			boolean activoParaPresupuesto) {
		
		if (presupuestoCategoria.getCategoria() == null || presupuestoCategoria.getCategoria().getIdCategoria() == null) {
			return new ResultadoResponse(false, "Debe seleccionar una categoría");
		}
		
		if (presupuestoCategoria.getMontoAsignado() == null || presupuestoCategoria.getMontoAsignado() <= 0) {
			return new ResultadoResponse(false, "El monto asignado debe ser mayor a 0");
		}
		
		var categoria = categoriaRepository.findById(presupuestoCategoria.getCategoria().getIdCategoria()).orElse(null);
		
		if (categoria == null) {
			return new ResultadoResponse(false, "La categoría seleccionada no existe");
		}
		
		if (!"gasto".equalsIgnoreCase(categoria.getTipo())) {
			return new ResultadoResponse(false, "Solo se pueden asignar categorías de gasto al presupuesto");
		}
		
		if (categoria.getActivo() == null || !categoria.getActivo()) {
			return new ResultadoResponse(false, "La categoría seleccionada no está activa");
		}
		
		if (!activoParaPresupuesto) {
			return new ResultadoResponse(true, "Validación correcta");
		}
		
		Double totalAsignado = calcularTotalAsignadoActivo(
				presupuesto.getIdPresupuesto(), 
				idDetalleActual
		);
		
		Double nuevoTotal = totalAsignado + presupuestoCategoria.getMontoAsignado();
		
		if (nuevoTotal > presupuesto.getMontoTotal()) {
			return new ResultadoResponse(false, "La suma asignada por categorías no puede superar el presupuesto total");
		}
		
		return new ResultadoResponse(true, "Validación correcta");
	}
	
	private Double calcularTotalAsignadoActivo(Integer idPresupuesto, Integer idDetalleActual) {
		Double total = 0.0;
		
		var detalles = presupuestoCategoriaRepository
				.findByPresupuestoIdPresupuestoOrderByIdPresupuestoCategoriaDesc(idPresupuesto);
		
		for (PresupuestoCategoria item : detalles) {
			
			boolean estaActivo = Boolean.TRUE.equals(item.getActivo());
			boolean esDetalleActual = idDetalleActual != null 
					&& item.getIdPresupuestoCategoria().equals(idDetalleActual);
			
			if (estaActivo && !esDetalleActual && item.getMontoAsignado() != null) {
				total += item.getMontoAsignado();
			}
		}
		
		return total;
	}
}