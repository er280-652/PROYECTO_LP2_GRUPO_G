package com.Finanzas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Finanzas.service.MetaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("movimiento")
public class MetaController {

	private final MetaService metaService;
	
	@GetMapping("metas")
	public String listado(Model model) {
		model.addAttribute("lstMovimientos", metaService.getAll());
		return "movimiento/metas";
	}
}
