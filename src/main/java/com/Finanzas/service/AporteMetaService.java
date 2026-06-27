package com.Finanzas.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.Finanzas.dto.ResultadoResponse;
import com.Finanzas.model.AporteMeta;
import com.Finanzas.model.Categoria;
import com.Finanzas.model.Meta;
import com.Finanzas.model.Movimiento;
import com.Finanzas.model.Usuario;
import com.Finanzas.repository.AporteMetaRepository;
import com.Finanzas.repository.CategoriaRepository;
import com.Finanzas.repository.MetaRepository;
import com.Finanzas.repository.MovimientoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AporteMetaService {

	private final AporteMetaRepository aporteMetaRepository;
	private final MetaRepository metaRepository;
	private final MovimientoRepository movimientoRepository;
	private final CategoriaRepository categoriaRepository;
	
	private static final String CATEGORIA_APORTE_META = "Aporte a meta";
	private static final String TIPO_INGRESO = "ingreso";
	
	public List<AporteMeta> getByMeta(Integer idMeta, Integer idUsuario) {
		var meta = metaRepository.findByIdMetaAndIdUsuario(idMeta, idUsuario);
		
		if (meta == null) {
			return List.of();
		}
		
		return aporteMetaRepository.findByMetaIdMetaOrderByIdAporteDesc(idMeta);
	}
	
	public AporteMeta getOne(Integer idAporte, Integer idUsuario) {
		return aporteMetaRepository.findByIdAporteAndIdUsuario(idAporte, idUsuario);
	}
	
	@Transactional
	public ResultadoResponse create(AporteMeta aporteMeta, Integer idMeta, Integer idUsuario) {
		try {
			var meta = metaRepository.findByIdMetaAndIdUsuario(idMeta, idUsuario);
			
			if (meta == null) {
				return new ResultadoResponse(false, "La meta no existe o no pertenece al usuario actual");
			}
			
			aporteMeta.setActivo(true);
			
			var validacion = validar(aporteMeta, meta, true);
			
			if (!validacion.success()) {
				return validacion;
			}
			
			var categoria = getCategoriaAporteMeta();
			
			if (categoria == null) {
				return new ResultadoResponse(false, "Debe existir una categoría activa de ingreso llamada Aporte a meta");
			}
			
			Meta metaBD = new Meta();
			metaBD.setIdMeta(idMeta);
			aporteMeta.setMeta(metaBD);
			
			var movimiento = crearMovimientoIngreso(aporteMeta, meta, categoria, idUsuario);
			aporteMeta.setMovimiento(movimiento);
			
			actualizarMontoMeta(meta, aporteMeta.getMontoAporte());
			
			var registro = aporteMetaRepository.save(aporteMeta);
			var mensaje = String.format("Aporte con ID %s registrado como ingreso", registro.getIdAporte());
			
			return new ResultadoResponse(true, mensaje);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultadoResponse(false, "Hubo un error en la transacción");
		}
	}
	
	@Transactional
	public ResultadoResponse update(AporteMeta aporteMeta, Integer idUsuario) {
		try {
			var original = aporteMetaRepository.findByIdAporteAndIdUsuario(aporteMeta.getIdAporte(), idUsuario);
			
			if (original == null) {
				return new ResultadoResponse(false, "El aporte no existe o no pertenece al usuario actual");
			}
			
			var meta = original.getMeta();
			var validacion = validar(aporteMeta, meta, false);
			
			if (!validacion.success()) {
				return validacion;
			}
			
			if (getCategoriaAporteMeta() == null) {
				return new ResultadoResponse(false, "Debe existir una categoría activa de ingreso llamada Aporte a meta");
			}
			
			if (Boolean.TRUE.equals(original.getActivo())) {
				Double montoAnterior = original.getMontoAporte() == null ? 0.0 : original.getMontoAporte();
				Double montoNuevo = aporteMeta.getMontoAporte() == null ? 0.0 : aporteMeta.getMontoAporte();
				Double diferencia = montoNuevo - montoAnterior;
				Double montoActual = meta.getMontoActual() == null ? 0.0 : meta.getMontoActual();
				
				if (montoActual + diferencia > meta.getMontoObjetivo()) {
					return new ResultadoResponse(false, "El aporte no puede superar el monto restante de la meta");
				}
				
				actualizarMontoMeta(meta, diferencia);
			}
			
			original.setFecha(aporteMeta.getFecha());
			original.setMontoAporte(aporteMeta.getMontoAporte());
			original.setObservacion(aporteMeta.getObservacion());
			
			var movimientoResponse = sincronizarMovimiento(original, meta, idUsuario);
			
			if (!movimientoResponse.success()) {
				return movimientoResponse;
			}
			
			var registro = aporteMetaRepository.save(original);
			var mensaje = String.format("Aporte con ID %s actualizado", registro.getIdAporte());
			
			return new ResultadoResponse(true, mensaje);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultadoResponse(false, "Hubo un error en la transacción");
		}
	}
	
	@Transactional
	public ResultadoResponse changeActive(Integer idAporte, Integer idUsuario) {
		try {
			var aporte = aporteMetaRepository.findByIdAporteAndIdUsuario(idAporte, idUsuario);
			
			if (aporte == null) {
				return new ResultadoResponse(false, "El aporte no existe o no pertenece al usuario actual");
			}
			
			var meta = aporte.getMeta();
			boolean nuevoEstado = !Boolean.TRUE.equals(aporte.getActivo());
			
			if (getCategoriaAporteMeta() == null) {
				return new ResultadoResponse(false, "Debe existir una categoría activa de ingreso llamada Aporte a meta");
			}
			
			if (nuevoEstado) {
				var validacion = validarActivacion(aporte, meta);
				
				if (!validacion.success()) {
					return validacion;
				}
				
				actualizarMontoMeta(meta, aporte.getMontoAporte());
			} else {
				Double monto = aporte.getMontoAporte() == null ? 0.0 : aporte.getMontoAporte();
				actualizarMontoMeta(meta, monto * -1);
			}
			
			aporte.setActivo(nuevoEstado);
			
			var movimientoResponse = sincronizarMovimiento(aporte, meta, idUsuario);
			
			if (!movimientoResponse.success()) {
				return movimientoResponse;
			}
			
			aporteMetaRepository.save(aporte);
			
			var estado = nuevoEstado ? "activado" : "desactivado";
			var mensaje = String.format("Aporte con ID %s %s", aporte.getIdAporte(), estado);
			
			return new ResultadoResponse(true, mensaje);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultadoResponse(false, "Hubo un error en la transacción");
		}
	}
	
	private ResultadoResponse validar(AporteMeta aporteMeta, Meta meta, boolean afectaMeta) {
		if (aporteMeta.getFecha() == null) {
			return new ResultadoResponse(false, "Debe ingresar la fecha del aporte");
		}
		
		if (aporteMeta.getMontoAporte() == null || aporteMeta.getMontoAporte() <= 0) {
			return new ResultadoResponse(false, "El monto del aporte debe ser mayor a 0");
		}
		
		if (meta.getFechaObjetivo() != null && LocalDate.now().isAfter(meta.getFechaObjetivo())) {
			return new ResultadoResponse(false, "No se puede registrar o editar aportes después de la fecha límite");
		}
		
		if (meta.getFechaObjetivo() != null && aporteMeta.getFecha().isAfter(meta.getFechaObjetivo())) {
			return new ResultadoResponse(false, "La fecha del aporte no puede ser posterior a la fecha límite de la meta");
		}
		
		if (afectaMeta) {
			Double montoActual = meta.getMontoActual() == null ? 0.0 : meta.getMontoActual();
			Double montoAporte = aporteMeta.getMontoAporte() == null ? 0.0 : aporteMeta.getMontoAporte();
			
			if (montoActual + montoAporte > meta.getMontoObjetivo()) {
				return new ResultadoResponse(false, "El aporte no puede superar el monto restante de la meta");
			}
		}
		
		return new ResultadoResponse(true, "Validación correcta");
	}
	
	private ResultadoResponse validarActivacion(AporteMeta aporteMeta, Meta meta) {
		if (meta.getFechaObjetivo() != null && LocalDate.now().isAfter(meta.getFechaObjetivo())) {
			return new ResultadoResponse(false, "No se puede activar aportes después de la fecha límite");
		}
		
		Double montoActual = meta.getMontoActual() == null ? 0.0 : meta.getMontoActual();
		Double montoAporte = aporteMeta.getMontoAporte() == null ? 0.0 : aporteMeta.getMontoAporte();
		
		if (montoActual + montoAporte > meta.getMontoObjetivo()) {
			return new ResultadoResponse(false, "No se puede activar porque el aporte superaría el monto objetivo de la meta");
		}
		
		return new ResultadoResponse(true, "Validación correcta");
	}
	
	private void actualizarMontoMeta(Meta meta, Double diferencia) {
		Double montoActual = meta.getMontoActual() == null ? 0.0 : meta.getMontoActual();
		Double nuevoMonto = montoActual + diferencia;
		
		if (nuevoMonto < 0) {
			nuevoMonto = 0.0;
		}
		
		meta.setMontoActual(nuevoMonto);
		meta.setCompletada(nuevoMonto >= meta.getMontoObjetivo());
	}
	
	private Categoria getCategoriaAporteMeta() {
		return categoriaRepository.findByDescripcionIgnoreCaseAndTipoIgnoreCaseAndActivoTrue(
				CATEGORIA_APORTE_META, 
				TIPO_INGRESO
		);
	}
	
	private Movimiento crearMovimientoIngreso(AporteMeta aporteMeta, Meta meta, Categoria categoria, Integer idUsuario) {
		Usuario usuario = new Usuario();
		usuario.setIdUsuario(idUsuario);
		
		Movimiento movimiento = new Movimiento();
		movimiento.setFecha(aporteMeta.getFecha());
		movimiento.setDescripcion("Aporte a meta: " + meta.getNombre());
		movimiento.setMonto(aporteMeta.getMontoAporte());
		movimiento.setActivo(Boolean.TRUE.equals(aporteMeta.getActivo()));
		movimiento.setCategoria(categoria);
		movimiento.setUsuario(usuario);
		
		return movimientoRepository.save(movimiento);
	}
	
	private ResultadoResponse sincronizarMovimiento(AporteMeta aporteMeta, Meta meta, Integer idUsuario) {
		var categoria = getCategoriaAporteMeta();
		
		if (categoria == null) {
			return new ResultadoResponse(false, "Debe existir una categoría activa de ingreso llamada Aporte a meta");
		}
		
		Movimiento movimiento = aporteMeta.getMovimiento();
		
		if (movimiento == null) {
			movimiento = crearMovimientoIngreso(aporteMeta, meta, categoria, idUsuario);
			aporteMeta.setMovimiento(movimiento);
			return new ResultadoResponse(true, "Movimiento creado");
		}
		
		Usuario usuario = new Usuario();
		usuario.setIdUsuario(idUsuario);
		
		movimiento.setFecha(aporteMeta.getFecha());
		movimiento.setDescripcion("Aporte a meta: " + meta.getNombre());
		movimiento.setMonto(aporteMeta.getMontoAporte());
		movimiento.setActivo(Boolean.TRUE.equals(aporteMeta.getActivo()));
		movimiento.setCategoria(categoria);
		movimiento.setUsuario(usuario);
		
		movimientoRepository.save(movimiento);
		
		return new ResultadoResponse(true, "Movimiento sincronizado");
	}
}
