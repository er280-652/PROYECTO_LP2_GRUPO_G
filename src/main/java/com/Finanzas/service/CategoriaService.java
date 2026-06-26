package com.Finanzas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Finanzas.dto.ResultadoResponse;
import com.Finanzas.model.Categoria;
import com.Finanzas.repository.CategoriaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<Categoria> getAll() {
        return categoriaRepository.findAll();
    }

    public List<Categoria> getByTipo(String tipo) {
        return categoriaRepository.findByTipo(tipo);
    }

    public Categoria getOne(Integer id) {
        return categoriaRepository.findById(id).orElseThrow();
    }

    public ResultadoResponse create(Categoria categoria) {
        try {
        	categoria.setActivo(true);
            categoriaRepository.save(categoria);
            return new ResultadoResponse(true, "Categoría registrada correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultadoResponse(false, "Error al registrar categoría");
        }
    }

    public ResultadoResponse update(Categoria categoria) {
        try {
        	
        	var categoriaBD = categoriaRepository.findById(categoria.getIdCategoria()).orElseThrow();
        	
        	categoria.setActivo(categoriaBD.getActivo());
        	
            categoriaRepository.save(categoria);
            return new ResultadoResponse(true, "Categoría actualizada correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultadoResponse(false, "Error al actualizar categoría");
        }
    }
    
    @Transactional
	public ResultadoResponse changeActive(Integer id) {
		try {
			var categoria = categoriaRepository.findById(id).orElseThrow();
			
			Boolean activo = categoria.getActivo();
			
			if (activo == null) {
				activo = true;
			}
			
			categoria.setActivo(!activo);
			
			var estado = categoria.getActivo() ? "Activo" : "desactivado";
			var mensaje = String.format("Usuario con ID %s %s", categoria.getIdCategoria(), estado);
			
			return new ResultadoResponse(true, mensaje);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultadoResponse(false, "Hubo un error en la transacción");
		}
	}
}