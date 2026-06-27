package com.Finanzas.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Finanzas.dto.PresupuestoFilter;
import com.Finanzas.model.Presupuesto;
import com.Finanzas.service.PresupuestoService;
import com.Finanzas.util.Alert;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("presupuesto")
public class PresupuestoController {

	private final PresupuestoService presupuestoService;
	
	@GetMapping("listado")
	public String listado(@ModelAttribute PresupuestoFilter filter, Model model, HttpSession session, RedirectAttributes flash) {
		String ruta = validarAcceso(session, flash);
		if (ruta != null) {
			return ruta;
		}
		
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		
		model.addAttribute("lstPresupuestos", presupuestoService.search(filter, idUsuario));
		model.addAttribute("meses", getMeses());
		model.addAttribute("filter", filter);
		
		return "presupuesto/listado";
	}
	
	@GetMapping("nuevo")
	public String nuevo(Model model, HttpSession session, RedirectAttributes flash) {
		String ruta = validarAcceso(session, flash);
		if (ruta != null) {
			return ruta;
		}
		
		Presupuesto presupuesto = new Presupuesto();
		presupuesto.setAnio(presupuestoService.getAnioActual());
		
		model.addAttribute("presupuesto", presupuesto);
		cargarDatosFormulario(model);
		
		return "presupuesto/nuevo";
	}
	
	@PostMapping("registrar")
	public String registrar(@ModelAttribute Presupuesto presupuesto, Model model, HttpSession session, RedirectAttributes flash) {
		String ruta = validarAcceso(session, flash);
		if (ruta != null) {
			return ruta;
		}
		
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		var response = presupuestoService.create(presupuesto, idUsuario);
		
		if (!response.success()) {
			model.addAttribute("presupuesto", presupuesto);
			cargarDatosFormulario(model);
			model.addAttribute("alert", Alert.sweetAlertError(response.mensaje()));
			return "presupuesto/nuevo";
		}
		
		var toast = Alert.sweetToast(response.mensaje(), "success", 5000);
		flash.addFlashAttribute("toast", toast);
		return "redirect:/presupuesto/listado";
	}
	
	@GetMapping("edicion/{id}")
	public String edicion(@PathVariable Integer id, Model model, HttpSession session, RedirectAttributes flash) {
		String ruta = validarAcceso(session, flash);
		if (ruta != null) {
			return ruta;
		}
		
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		var presupuesto = presupuestoService.getOne(id, idUsuario);
		
		if (presupuesto == null) {
			flash.addFlashAttribute("toast", Alert.sweetToast("Presupuesto no encontrado", "error", 5000));
			return "redirect:/presupuesto/listado";
		}
		
		model.addAttribute("presupuesto", presupuesto);
		cargarDatosFormulario(model);
		
		return "presupuesto/edicion";
	}
	
	@PostMapping("guardar")
	public String guardar(@ModelAttribute Presupuesto presupuesto, Model model, HttpSession session, RedirectAttributes flash) {
		String ruta = validarAcceso(session, flash);
		if (ruta != null) {
			return ruta;
		}
		
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		var response = presupuestoService.update(presupuesto, idUsuario);
		
		if (!response.success()) {
			model.addAttribute("presupuesto", presupuesto);
			cargarDatosFormulario(model);
			model.addAttribute("alert", Alert.sweetAlertError(response.mensaje()));
			return "presupuesto/edicion";
		}
		
		var toast = Alert.sweetToast(response.mensaje(), "success", 5000);
		flash.addFlashAttribute("toast", toast);
		return "redirect:/presupuesto/listado";
	}
	
	private void cargarDatosFormulario(Model model) {
		model.addAttribute("meses", getMeses());
		model.addAttribute("anioActual", presupuestoService.getAnioActual());
	}
	
	private List<String> getMeses() {
		return List.of("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre");
	}
	
	private String validarAcceso(HttpSession session, RedirectAttributes flash) {
		if (session.getAttribute("idUsuario") == null) {
			return "redirect:/";
		}
		
		Object idTipo = session.getAttribute("idTipo");
		
		if (idTipo == null || !idTipo.toString().equals("2")) {
			flash.addFlashAttribute("toast", Alert.sweetToast("La sección presupuesto solo está disponible para usuarios", "error", 5000));
			return "redirect:/dashboard";
		}
		
		return null;
	}
}
