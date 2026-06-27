package com.Finanzas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Finanzas.dto.ResultadoResponse;
import com.Finanzas.model.Movimiento;
import com.Finanzas.repository.AporteMetaRepository;
import com.Finanzas.repository.MovimientoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final AporteMetaRepository aporteMetaRepository;

    public List<Movimiento> getAll() {
        return movimientoRepository.findAllByOrderByIdMovimientoDesc();
    }

    public List<Movimiento> getByTipo(String tipo) {
        return movimientoRepository.findByCategoriaTipoIgnoreCaseOrderByIdMovimientoDesc(tipo);
    }
    
    public List<Movimiento> getByUsuario(Integer idUsuario) {
		return movimientoRepository.findByUsuario(idUsuario);
	}
    

    public Movimiento getOne(Integer id) {
        return movimientoRepository.findById(id).orElseThrow();
    }

    public ResultadoResponse create(Movimiento movimiento) {
        try {
            movimiento.setActivo(true);
            movimientoRepository.save(movimiento);

            return new ResultadoResponse(true, "Movimiento registrado correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultadoResponse(false, "Error al registrar movimiento");
        }
    }

    public ResultadoResponse update(Movimiento movimiento) {
        try {
            if (movimiento.getIdMovimiento() != null 
                    && aporteMetaRepository.existsByMovimientoIdMovimiento(movimiento.getIdMovimiento())) {
                return new ResultadoResponse(false, "Este movimiento pertenece a un aporte de meta y debe editarse desde Aportes");
            }
            
            movimiento.setActivo(true);
            movimientoRepository.save(movimiento);

            return new ResultadoResponse(true, "Movimiento actualizado correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultadoResponse(false, "Error al actualizar movimiento");
        }
    }

    @Transactional
    public ResultadoResponse desactivar(Integer id) {
        try {
            if (aporteMetaRepository.existsByMovimientoIdMovimiento(id)) {
                return new ResultadoResponse(false, "Este movimiento pertenece a un aporte de meta y debe activarse o desactivarse desde Aportes");
            }
            
            var movimiento = movimientoRepository.findById(id).orElseThrow();

            movimiento.setActivo(!movimiento.getActivo());

            String estado = movimiento.getActivo() ? "activado" : "desactivado";

            return new ResultadoResponse(true, "Movimiento " + estado + " correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultadoResponse(false, "Error al cambiar estado del movimiento");
        }
    }

}
