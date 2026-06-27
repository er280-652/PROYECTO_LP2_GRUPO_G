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

import com.Finanzas.model.Usuario;
import com.Finanzas.service.TipoService;
import com.Finanzas.service.UsuarioService;
import com.Finanzas.util.Alert;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("usuario")
public class UsuarioController {

	private final UsuarioService usuarioService;
	private final TipoService tipoService;
	
	@GetMapping("listado")
	public String listado(Model model) {
		model.addAttribute("usuarios", usuarioService.getAll());
		model.addAttribute("tipos", tipoService.getAll());
		
		return "usuario/listado";
	}
	
	@GetMapping("nuevo")
	public String nuevo(Model model) {
		model.addAttribute("usuario", new Usuario());
		model.addAttribute("tipos", tipoService.getAll());

		return "usuario/nuevo";
	}
	
	@PostMapping("registrar")
	public String registrar(@ModelAttribute Usuario usuario, Model model, RedirectAttributes flash) {
		var response = usuarioService.create(usuario);
		
		if (!response.success()) {
			model.addAttribute("usuario", usuario);
			model.addAttribute("tipos", tipoService.getAll());
			model.addAttribute("alert", Alert.sweetAlertError(response.mensaje()));
			
			return "usuario/nuevo";
		}
		var toast = Alert.sweetToast(response.mensaje(), "success", 5000);
		flash.addFlashAttribute("toast", toast);
		return "redirect:/usuario/listado";
	}
	
	 @GetMapping("edicion/{id}")
	    public String edicion(@PathVariable Integer id, Model model) {

	        model.addAttribute("usuario", usuarioService.getOne(id));
	        model.addAttribute("tipos", tipoService.getAll());

	        return "usuario/edicion";
	    }
	 
	 @PostMapping("guardar")
	    public String guardar(@ModelAttribute Usuario usuario,  Model model, RedirectAttributes flash) {
	        var response = usuarioService.update(usuario);

	        if (!response.success()) {

	            model.addAttribute("usuario", usuario);
	            model.addAttribute("tipos", tipoService.getAll());
	            model.addAttribute("alert",Alert.sweetAlertError(response.mensaje()));

	            return "usuario/edicion";
	        }

	        var toast = Alert.sweetToast(response.mensaje(), "success", 5000);
	        flash.addFlashAttribute("toast", toast);

	        return "redirect:/usuario/listado";
	    }
	
	 @PostMapping("cambiar-estado")
	    public String cambiarEstado(@RequestParam Integer id,RedirectAttributes flash) {

	        var response = usuarioService.changeActive(id);

	        var toast = Alert.sweetToast(response.mensaje(), "success", 5000);
	        flash.addFlashAttribute("toast", toast);

	        return "redirect:/usuario/listado";
	    }
	
	
}
