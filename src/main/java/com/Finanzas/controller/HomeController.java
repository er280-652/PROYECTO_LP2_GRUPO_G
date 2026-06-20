package com.Finanzas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	 @GetMapping("/")
	    public String inicio() {
	        return "redirect:/dashboard";
	    }

	    @GetMapping("/dashboard")
	    public String dashboard() {
	        return "movimiento/dashboard";
	    }
}
