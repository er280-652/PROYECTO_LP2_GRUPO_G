package com.Finanzas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.Finanzas.model.Movimiento;
import com.Finanzas.model.Usuario;
import com.Finanzas.service.CategoriaService;
import com.Finanzas.service.MovimientoService;
import com.Finanzas.util.Alert;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("movimiento")
public class MovimientoController {

    private final MovimientoService movimientoService;
    private final CategoriaService categoriaService;

    @GetMapping("listado")
    public String listado(Model model) {
        model.addAttribute("lstMovimiento", movimientoService.getAll());
        return "movimiento/movimientosListado";
    }

    @GetMapping("ingresos")
    public String ingresos(Model model) {
        model.addAttribute("lstMovimiento", movimientoService.getByTipo("INGRESO"));
        return "movimiento/movimientosListado";
    }

    @GetMapping("gastos")
    public String gastos(Model model) {
        model.addAttribute("lstMovimiento", movimientoService.getByTipo("GASTO"));
        return "movimiento/movimientosListado";
    }

    @GetMapping("nuevo")
    public String nuevo(Model model) {
        model.addAttribute("movimiento", new Movimiento());
        model.addAttribute("categorias", categoriaService.getAll());
        return "movimiento/movimientoNuevo";
    }

    @PostMapping("registrar")
    public String registrar(@ModelAttribute Movimiento movimiento,
                            HttpSession session,
                            RedirectAttributes flash) {

        Integer idUsuario = (Integer) session.getAttribute("idUsuario");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        movimiento.setUsuario(usuario);

        var response = movimientoService.create(movimiento);

        flash.addFlashAttribute("toast",
                Alert.sweetToast(response.mensaje(), "success", 5000));

        return "redirect:/movimiento/listado";
    }

    @GetMapping("edicion/{id}")
    public String edicion(@PathVariable Integer id, Model model) {
        model.addAttribute("movimiento", movimientoService.getOne(id));
        model.addAttribute("categorias", categoriaService.getAll());
        return "movimiento/movimientoEdicion";
    }

    @PostMapping("guardar")
    public String guardar(@ModelAttribute Movimiento movimiento,
                          HttpSession session,
                          RedirectAttributes flash) {

        Integer idUsuario = (Integer) session.getAttribute("idUsuario");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        movimiento.setUsuario(usuario);

        var response = movimientoService.update(movimiento);
        var icon = response.success() ? "success" : "error";

        flash.addFlashAttribute("toast",
                Alert.sweetToast(response.mensaje(), icon, 5000));

        return "redirect:/movimiento/listado";
    }

  
    @PostMapping("desactivar")
    public String desactivar(@RequestParam Integer id, RedirectAttributes flash) {
        var response = movimientoService.desactivar(id);
        var icon = response.success() ? "success" : "error";
        flash.addFlashAttribute("toast",
                Alert.sweetToast(response.mensaje(), icon, 5000));
        return "redirect:/movimiento/listado";
    }
    
    @GetMapping("ingresos/nuevo")
    public String nuevoIngreso(Model model) {
        model.addAttribute("movimiento", new Movimiento());
        model.addAttribute("categorias", categoriaService.getByTipo("INGRESO"));
        model.addAttribute("tituloFormulario", "Nuevo Ingreso");
        model.addAttribute("rutaFormulario", "/movimiento/ingresos/registrar");
        return "movimiento/ingresoNuevo";
    }

    @PostMapping("ingresos/registrar")
    public String registrarIngreso(@ModelAttribute Movimiento movimiento,
                                   HttpSession session,
                                   RedirectAttributes flash) {

        Integer idUsuario = (Integer) session.getAttribute("idUsuario");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);

        movimiento.setUsuario(usuario);

        var response = movimientoService.create(movimiento);

        flash.addFlashAttribute("toast",
                Alert.sweetToast(response.mensaje(), "success", 5000));

        return "redirect:/movimiento/listado";
    }
    
    @GetMapping("gastos/nuevo")
    public String nuevoGasto(Model model) {
        model.addAttribute("movimiento", new Movimiento());
        model.addAttribute("categorias", categoriaService.getByTipo("GASTO"));
        model.addAttribute("tituloFormulario", "Nuevo Gasto");
        model.addAttribute("rutaFormulario", "/movimiento/gastos/registrar");
        return "movimiento/gastoNuevo";
    }

    @PostMapping("gastos/registrar")
    public String registrarGasto(@ModelAttribute Movimiento movimiento,
                                 HttpSession session,
                                 RedirectAttributes flash) {

        Integer idUsuario = (Integer) session.getAttribute("idUsuario");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);

        movimiento.setUsuario(usuario);

        var response = movimientoService.create(movimiento);

        flash.addFlashAttribute("toast",
                Alert.sweetToast(response.mensaje(), "success", 5000));

        return "redirect:/movimiento/listado";
    }
}