package com.speedmedical.controller;

import com.speedmedical.entity.Usuario;
import com.speedmedical.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        // Carrega o usuário logado para mostrar "Olá, Nome" na navbar
        if (principal != null) {
            String loginUsuario = principal.getName();
            Usuario usuario = usuarioRepository.findByUsernameOrCpf(loginUsuario, loginUsuario);
            model.addAttribute("usuario", usuario);
        }

        // --- CORREÇÃO DO CAMINHO AQUI ---
        // O Spring vai procurar em: src/main/resources/templates/pages/home.html
        return "pages/home"; 
    }
    
    // Redireciona a raiz "/" para o "/home" se estiver logado
    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }
}