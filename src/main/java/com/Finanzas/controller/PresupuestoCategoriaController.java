package com.Finanzas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Finanzas.model.Categoria;
import com.Finanzas.model.Presupuesto;
import com.Finanzas.model.PresupuestoCategoria;
import com.Finanzas.service.CategoriaService;
import com.Finanzas.service.PresupuestoCategoriaService;
import com.Finanzas.service.PresupuestoService;
import com.Finanzas.util.Alert;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("presupuesto-categoria")
public class PresupuestoCategoriaController {

	private final PresupuestoCategoriaService presupuestoCategoriaService;
	private final PresupuestoService presupuestoService;
	private final CategoriaService categoriaService;
	
	@GetMapping("listado/{idPresupuesto}")
	public String listado(@PathVariable Integer idPresupuesto, Model model, HttpSession session, RedirectAttributes flash) {
		String ruta = validarAcceso(session, flash);
		if (ruta != null) {
			return ruta;
		}
		
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		var presupuesto = presupuestoService.getOne(idPresupuesto, idUsuario);
		
		if (presupuesto == null) {
			flash.addFlashAttribute("toast", Alert.sweetToast("Presupuesto no encontrado", "error", 5000));
			return "redirect:/presupuesto/listado";
		}
		
		model.addAttribute("presupuesto", presupuesto);
		model.addAttribute("lstPresupuestoCategoria", presupuestoCategoriaService.getByPresupuesto(idPresupuesto, idUsuario));
		model.addAttribute("totalAsignado", presupuestoCategoriaService.getTotalAsignado(idPresupuesto, idUsuario));
		model.addAttribute("saldoDisponible", presupuestoCategoriaService.getSaldoDisponible(idPresupuesto, idUsuario));
		
		return "presupuesto-categoria/listado";
	}
	
	@GetMapping("nuevo/{idPresupuesto}")
	public String nuevo(@PathVariable Integer idPresupuesto, Model model, HttpSession session, RedirectAttributes flash) {
		String ruta = validarAcceso(session, flash);
		if (ruta != null) {
			return ruta;
		}
		
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		var presupuesto = presupuestoService.getOne(idPresupuesto, idUsuario);
		
		if (presupuesto == null) {
			flash.addFlashAttribute("toast", Alert.sweetToast("Presupuesto no encontrado", "error", 5000));
			return "redirect:/presupuesto/listado";
		}
		
		PresupuestoCategoria presupuestoCategoria = new PresupuestoCategoria();
		Presupuesto presupuestoForm = new Presupuesto();
		presupuestoForm.setIdPresupuesto(idPresupuesto);
		presupuestoCategoria.setPresupuesto(presupuestoForm);
		presupuestoCategoria.setCategoria(new Categoria());
		
		model.addAttribute("presupuestoCategoria", presupuestoCategoria);
		cargarDatosFormulario(model, presupuesto);
		
		return "presupuesto-categoria/nuevo";
	}
	
	@PostMapping("registrar")
	public String registrar(@ModelAttribute PresupuestoCategoria presupuestoCategoria, Model model, HttpSession session, RedirectAttributes flash) {
		String ruta = validarAcceso(session, flash);
		if (ruta != null) {
			return ruta;
		}
		
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		
		if (presupuestoCategoria.getPresupuesto() == null || presupuestoCategoria.getPresupuesto().getIdPresupuesto() == null) {
			flash.addFlashAttribute("toast", Alert.sweetToast("Presupuesto no encontrado", "error", 5000));
			return "redirect:/presupuesto/listado";
		}
		
		Integer idPresupuesto = presupuestoCategoria.getPresupuesto().getIdPresupuesto();
		var presupuesto = presupuestoService.getOne(idPresupuesto, idUsuario);
		
		if (presupuesto == null) {
			flash.addFlashAttribute("toast", Alert.sweetToast("Presupuesto no encontrado", "error", 5000));
			return "redirect:/presupuesto/listado";
		}
		
		var response = presupuestoCategoriaService.create(presupuestoCategoria, idPresupuesto, idUsuario);
		
		if (!response.success()) {
			if (presupuestoCategoria.getCategoria() == null) {
				presupuestoCategoria.setCategoria(new Categoria());
			}
			model.addAttribute("presupuestoCategoria", presupuestoCategoria);
			cargarDatosFormulario(model, presupuesto);
			model.addAttribute("alert", Alert.sweetAlertError(response.mensaje()));
			return "presupuesto-categoria/nuevo";
		}
		
		var toast = Alert.sweetToast(response.mensaje(), "success", 5000);
		flash.addFlashAttribute("toast", toast);
		return "redirect:/presupuesto-categoria/listado/" + idPresupuesto;
	}
	
	@GetMapping("edicion/{id}")
	public String edicion(@PathVariable Integer id, Model model, HttpSession session, RedirectAttributes flash) {
		String ruta = validarAcceso(session, flash);
		if (ruta != null) {
			return ruta;
		}
		
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		var presupuestoCategoria = presupuestoCategoriaService.getOne(id, idUsuario);
		
		if (presupuestoCategoria == null) {
			flash.addFlashAttribute("toast", Alert.sweetToast("Detalle de presupuesto no encontrado", "error", 5000));
			return "redirect:/presupuesto/listado";
		}
		
		model.addAttribute("presupuestoCategoria", presupuestoCategoria);
		cargarDatosFormulario(model, presupuestoCategoria.getPresupuesto());
		
		return "presupuesto-categoria/edicion";
	}
	
	@PostMapping("guardar")
	public String guardar(@ModelAttribute PresupuestoCategoria presupuestoCategoria, Model model, HttpSession session, RedirectAttributes flash) {
		String ruta = validarAcceso(session, flash);
		if (ruta != null) {
			return ruta;
		}
		
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		var original = presupuestoCategoriaService.getOne(presupuestoCategoria.getIdPresupuestoCategoria(), idUsuario);
		
		if (original == null) {
			flash.addFlashAttribute("toast", Alert.sweetToast("Detalle de presupuesto no encontrado", "error", 5000));
			return "redirect:/presupuesto/listado";
		}
		
		var response = presupuestoCategoriaService.update(presupuestoCategoria, idUsuario);
		Integer idPresupuesto = original.getPresupuesto().getIdPresupuesto();
		
		if (!response.success()) {
			if (presupuestoCategoria.getCategoria() == null) {
				presupuestoCategoria.setCategoria(new Categoria());
			}
			presupuestoCategoria.setPresupuesto(original.getPresupuesto());
			model.addAttribute("presupuestoCategoria", presupuestoCategoria);
			cargarDatosFormulario(model, original.getPresupuesto());
			model.addAttribute("alert", Alert.sweetAlertError(response.mensaje()));
			return "presupuesto-categoria/edicion";
		}
		
		var toast = Alert.sweetToast(response.mensaje(), "success", 5000);
		flash.addFlashAttribute("toast", toast);
		return "redirect:/presupuesto-categoria/listado/" + idPresupuesto;
	}
	
	@PostMapping("cambiar-estado")
	public String cambiarEstado(@RequestParam Integer id, HttpSession session, RedirectAttributes flash) {
		String ruta = validarAcceso(session, flash);
		if (ruta != null) {
			return ruta;
		}
		
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		var presupuestoCategoria = presupuestoCategoriaService.getOne(id, idUsuario);
		
		if (presupuestoCategoria == null) {
			flash.addFlashAttribute("toast", Alert.sweetToast("Detalle de presupuesto no encontrado", "error", 5000));
			return "redirect:/presupuesto/listado";
		}
		
		Integer idPresupuesto = presupuestoCategoria.getPresupuesto().getIdPresupuesto();
		var response = presupuestoCategoriaService.changeActive(id, idUsuario);
		var icon = response.success() ? "success" : "error";
		
		flash.addFlashAttribute("toast", Alert.sweetToast(response.mensaje(), icon, 5000));
		return "redirect:/presupuesto-categoria/listado/" + idPresupuesto;
	}

	private void cargarDatosFormulario(Model model, Presupuesto presupuesto) {
		model.addAttribute("presupuesto", presupuesto);
		model.addAttribute("categorias", categoriaService.getGastosActivos());
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
