package com.Finanzas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Finanzas.dto.AutenticacionFilter;
import com.Finanzas.service.AutenticacionService;
import com.Finanzas.util.Alert;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("login")
@RequiredArgsConstructor
public class LoginController {

	private final AutenticacionService autenticacionService;
	
	@GetMapping("iniciar-sesion")
	public String iniciarSesion(@ModelAttribute AutenticacionFilter filter,Model model, RedirectAttributes flash, HttpSession httpSession) {
		
		var usuario = autenticacionService.autenticathe(filter);
		
		if (usuario == null) {
			var mensaje = Alert.sweetAlertError("Cuenta o contraseña incorrecta");
			model.addAttribute("alert", mensaje);
			model.addAttribute("filter", filter);
			
			return "login";
		}
		
		if (!usuario.getActivo()) {
			var mensaje = Alert.sweetAlertError("La cuenta se encuentra inactiva");
			model.addAttribute("alert", mensaje);
			model.addAttribute("filter", filter);
			
			return "login";
		}
		
		httpSession.setAttribute("idUsuario", usuario.getIdUsuario());
		httpSession.setAttribute("fullName", usuario.getFullName());
		httpSession.setAttribute("idTipo", usuario.getTipo().getIdTipo());
		httpSession.setAttribute("tipoDescripcion", usuario.getTipo().getDescripcion());
		


		String alert = Alert.sweetImageUrl("Bienvenido " + usuario.getFullName(),"a tu<br><strong>Plataforma de Gestión Financiera", "/imagenes/ironMaiden.gif");
		flash.addFlashAttribute("alert", alert);
		
		return "redirect:/dashboard";
	}
}
