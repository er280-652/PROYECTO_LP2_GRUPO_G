package com.Finanzas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Finanzas.service.CategoriaService;
import com.Finanzas.service.MovimientoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("categoria")
public class CategoriaController {

	private final CategoriaService categoriaService;
	
	@GetMapping("listado")
	public String listado(Model model) {
	    model.addAttribute("lstCategorias", categoriaService.getAll());
	    return "movimiento/listadoCategoria";
	}
}
