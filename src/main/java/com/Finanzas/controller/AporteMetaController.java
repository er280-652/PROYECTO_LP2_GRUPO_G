package com.Finanzas.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Finanzas.model.AporteMeta;
import com.Finanzas.model.Meta;
import com.Finanzas.service.AporteMetaService;
import com.Finanzas.service.MetaService;
import com.Finanzas.util.Alert;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("aporte-meta")
public class AporteMetaController {

	private final AporteMetaService aporteMetaService;
	private final MetaService metaService;
	
	@GetMapping("listado/{idMeta}")
	public String listado(@PathVariable Integer idMeta, Model model, HttpSession session, RedirectAttributes flash) {
		String ruta = validarAcceso(session, flash);
		
		if (ruta != null) {
			return ruta;
		}
		
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		var meta = metaService.getOne(idMeta, idUsuario);
		
		if (meta == null) {
			flash.addFlashAttribute("toast", Alert.sweetToast("Meta no encontrada", "error", 5000));
			return "redirect:/meta/listado";
		}
		
		model.addAttribute("meta", meta);
		model.addAttribute("lstAportes", aporteMetaService.getByMeta(idMeta, idUsuario));
		
		return "aporte-meta/listado";
	}
	
	@GetMapping("nuevo/{idMeta}")
	public String nuevo(@PathVariable Integer idMeta, Model model, HttpSession session, RedirectAttributes flash) {
		String ruta = validarAcceso(session, flash);
		
		if (ruta != null) {
			return ruta;
		}
		
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		var meta = metaService.getOne(idMeta, idUsuario);
		
		if (meta == null) {
			flash.addFlashAttribute("toast", Alert.sweetToast("Meta no encontrada", "error", 5000));
			return "redirect:/meta/listado";
		}
		
		if (Boolean.TRUE.equals(meta.getFechaLimitePasada())) {
			flash.addFlashAttribute("toast", Alert.sweetToast("No se puede registrar aportes después de la fecha límite", "error", 5000));
			return "redirect:/aporte-meta/listado/" + idMeta;
		}
		
		if (Boolean.TRUE.equals(meta.getCompletada())) {
			flash.addFlashAttribute("toast", Alert.sweetToast("La meta ya se encuentra completada", "error", 5000));
			return "redirect:/aporte-meta/listado/" + idMeta;
		}
		
		AporteMeta aporteMeta = new AporteMeta();
		Meta metaForm = new Meta();
		metaForm.setIdMeta(idMeta);
		aporteMeta.setMeta(metaForm);
		aporteMeta.setFecha(LocalDate.now());
		
		model.addAttribute("aporteMeta", aporteMeta);
		model.addAttribute("meta", meta);
		
		return "aporte-meta/nuevo";
	}
	
	@PostMapping("registrar")
	public String registrar(@ModelAttribute AporteMeta aporteMeta, Model model, HttpSession session, RedirectAttributes flash) {
		String ruta = validarAcceso(session, flash);
		
		if (ruta != null) {
			return ruta;
		}
		
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		
		if (aporteMeta.getMeta() == null || aporteMeta.getMeta().getIdMeta() == null) {
			flash.addFlashAttribute("toast", Alert.sweetToast("Meta no encontrada", "error", 5000));
			return "redirect:/meta/listado";
		}
		
		Integer idMeta = aporteMeta.getMeta().getIdMeta();
		var meta = metaService.getOne(idMeta, idUsuario);
		
		if (meta == null) {
			flash.addFlashAttribute("toast", Alert.sweetToast("Meta no encontrada", "error", 5000));
			return "redirect:/meta/listado";
		}
		
		var response = aporteMetaService.create(aporteMeta, idMeta, idUsuario);
		
		if (!response.success()) {
			aporteMeta.setMeta(meta);
			model.addAttribute("aporteMeta", aporteMeta);
			model.addAttribute("meta", meta);
			model.addAttribute("alert", Alert.sweetAlertError(response.mensaje()));
			return "aporte-meta/nuevo";
		}
		
		var toast = Alert.sweetToast(response.mensaje(), "success", 5000);
		flash.addFlashAttribute("toast", toast);
		return "redirect:/aporte-meta/listado/" + idMeta;
	}
	
	@GetMapping("edicion/{id}")
	public String edicion(@PathVariable Integer id, Model model, HttpSession session, RedirectAttributes flash) {
		String ruta = validarAcceso(session, flash);
		
		if (ruta != null) {
			return ruta;
		}
		
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		var aporteMeta = aporteMetaService.getOne(id, idUsuario);
		
		if (aporteMeta == null) {
			flash.addFlashAttribute("toast", Alert.sweetToast("Aporte no encontrado", "error", 5000));
			return "redirect:/meta/listado";
		}
		
		model.addAttribute("aporteMeta", aporteMeta);
		model.addAttribute("meta", aporteMeta.getMeta());
		
		return "aporte-meta/edicion";
	}
	
	@PostMapping("guardar")
	public String guardar(@ModelAttribute AporteMeta aporteMeta, Model model, HttpSession session, RedirectAttributes flash) {
		String ruta = validarAcceso(session, flash);
		
		if (ruta != null) {
			return ruta;
		}
		
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		var original = aporteMetaService.getOne(aporteMeta.getIdAporte(), idUsuario);
		
		if (original == null) {
			flash.addFlashAttribute("toast", Alert.sweetToast("Aporte no encontrado", "error", 5000));
			return "redirect:/meta/listado";
		}
		
		Integer idMeta = original.getMeta().getIdMeta();
		var response = aporteMetaService.update(aporteMeta, idUsuario);
		
		if (!response.success()) {
			aporteMeta.setMeta(original.getMeta());
			model.addAttribute("aporteMeta", aporteMeta);
			model.addAttribute("meta", original.getMeta());
			model.addAttribute("alert", Alert.sweetAlertError(response.mensaje()));
			return "aporte-meta/edicion";
		}
		
		var toast = Alert.sweetToast(response.mensaje(), "success", 5000);
		flash.addFlashAttribute("toast", toast);
		return "redirect:/aporte-meta/listado/" + idMeta;
	}
	
	@PostMapping("cambiar-estado")
	public String cambiarEstado(@RequestParam Integer id, HttpSession session, RedirectAttributes flash) {
		String ruta = validarAcceso(session, flash);
		
		if (ruta != null) {
			return ruta;
		}
		
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		var aporteMeta = aporteMetaService.getOne(id, idUsuario);
		
		if (aporteMeta == null) {
			flash.addFlashAttribute("toast", Alert.sweetToast("Aporte no encontrado", "error", 5000));
			return "redirect:/meta/listado";
		}
		
		Integer idMeta = aporteMeta.getMeta().getIdMeta();
		var response = aporteMetaService.changeActive(id, idUsuario);
		var icon = response.success() ? "success" : "error";
		
		flash.addFlashAttribute("toast", Alert.sweetToast(response.mensaje(), icon, 5000));
		return "redirect:/aporte-meta/listado/" + idMeta;
	}
	
	private String validarAcceso(HttpSession session, RedirectAttributes flash) {
		if (session.getAttribute("idUsuario") == null) {
			return "redirect:/";
		}
		
		Object idTipo = session.getAttribute("idTipo");
		
		if (idTipo == null || !idTipo.toString().equals("2")) {
			flash.addFlashAttribute("toast", Alert.sweetToast("La sección metas solo está disponible para usuarios", "error", 5000));
			return "redirect:/dashboard";
		}
		
		return null;
	}
}
