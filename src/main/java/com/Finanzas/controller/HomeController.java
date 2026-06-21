package com.Finanzas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.Finanzas.dto.AutenticacionFilter;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	 @GetMapping("/")
	    public String home(Model model) {
		 	model.addAttribute("filter", new AutenticacionFilter());
	        return "login";
	    }

	    @GetMapping("/dashboard")
	    public String dashboard(HttpSession httpSession) {
	    	if (httpSession.getAttribute("idUsuario")== null) {
				return "redirect:/login/iniciar-sesion";
			}
	        return "dashboard";
	    }
	    
	    @GetMapping("/logout")
	    public String logout(HttpSession httpSession) {
			httpSession.invalidate();
			return "redirect:/";
		}
}
