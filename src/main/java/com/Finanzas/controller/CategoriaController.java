package com.Finanzas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Finanzas.model.Categoria;
import com.Finanzas.service.CategoriaService;
import com.Finanzas.util.Alert;

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

    @GetMapping("nuevo")
    public String nuevo(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "movimiento/categoriaNuevo";
    }

    @PostMapping("registrar")
    public String registrar(@ModelAttribute Categoria categoria,
                            RedirectAttributes flash) {

        var response = categoriaService.create(categoria);

        flash.addFlashAttribute("toast",
                Alert.sweetToast(response.mensaje(), "success", 5000));

        return "redirect:/categoria/listado";
    }

    @GetMapping("edicion/{id}")
    public String edicion(@PathVariable Integer id, Model model) {
        model.addAttribute("categoria", categoriaService.getOne(id));
        return "movimiento/categoriaEdicion";
    }

    @PostMapping("guardar")
    public String guardar(@ModelAttribute Categoria categoria,
                          RedirectAttributes flash) {

        var response = categoriaService.update(categoria);

        flash.addFlashAttribute("toast",
                Alert.sweetToast(response.mensaje(), "success", 5000));

        return "redirect:/categoria/listado";
    }
}