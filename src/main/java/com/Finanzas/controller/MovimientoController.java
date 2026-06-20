package com.Finanzas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Finanzas.service.MovimientoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("movimiento")
public class MovimientoController {

	private final MovimientoService movimientoService;
	
	@GetMapping("listado")
	public String listado(Model model) {
		model.addAttribute("lstMovimientos", movimientoService.getAll());
		return "finanzas/listado";
	}
}
