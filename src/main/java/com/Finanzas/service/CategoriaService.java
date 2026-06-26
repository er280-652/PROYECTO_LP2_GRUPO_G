package com.Finanzas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Finanzas.dto.ResultadoResponse;
import com.Finanzas.model.Categoria;
import com.Finanzas.repository.CategoriaRepository;

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
            categoriaRepository.save(categoria);
            return new ResultadoResponse(true, "Categoría registrada correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultadoResponse(false, "Error al registrar categoría");
        }
    }

    public ResultadoResponse update(Categoria categoria) {
        try {
            categoriaRepository.save(categoria);
            return new ResultadoResponse(true, "Categoría actualizada correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultadoResponse(false, "Error al actualizar categoría");
        }
    }
}