package com.example.Integrador.controladores;

import com.example.Integrador.modelos.Carrito;
import com.example.Integrador.modelos.EventoVisita;
import com.example.Integrador.modelos.Usuario;
import com.example.Integrador.repositorios.CarritoRepository;
import com.example.Integrador.repositorios.EventoVisitaRepository;
import com.example.Integrador.repositorios.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UsuarioController {

    @Autowired
    private EventoVisitaRepository eventoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @PostMapping("/iniciar-sesion")
    public String login(@RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null ||
                usuario.getPassword() == null ||
                !usuario.getPassword().trim().equals(password.trim())) {

            model.addAttribute("error", "Credenciales incorrectas");
            return "miCuenta";
        }

        System.out.println("LOGIN SESSION ID: " + session.getId());
        session.setAttribute("usuario", usuario);

        EventoVisita ev = new EventoVisita();
        ev.setUsuario(usuario);
        ev.setPagina("LOGIN");
        ev.setSesionId(session.getId());
        ev.setFecha(LocalDateTime.now());

        eventoRepository.save(ev);

        return "redirect:/miCuenta";
    }

    // REGISTRO
    @PostMapping("/registro")
    public String registrar(
            @ModelAttribute Usuario u,
            RedirectAttributes redirectAttributes) {

        if (usuarioRepository.existsByEmail(u.getEmail())) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El email ya se encuentra registrado");

            return "redirect:/miCuenta";
        }

        if (usuarioRepository.existsByDni(u.getDni())) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El DNI ya se encuentra registrado");

            return "redirect:/micuenta";
        }

        usuarioRepository.save(u);

        redirectAttributes.addFlashAttribute(
                "success",
                "Cuenta creada correctamente");

        return "redirect:/miCuenta";
    }

    // CERRRA SESION
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();

        return "redirect:/miCuenta";

    }

    // ELIMINAR CUENTA
    @PostMapping("/usuario/eliminar")
    @ResponseBody
    public Map<String, Object> eliminar(HttpSession session) {

        Map<String, Object> res = new HashMap<>();

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario != null) {
            usuarioRepository.deleteById(usuario.getId());
            session.invalidate();
            res.put("ok", true);
        } else {
            res.put("ok", false);
            res.put("error", "No hay sesión");
        }

        return res;
    }
}