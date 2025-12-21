package com.speedmedical.controller;

import com.speedmedical.entity.Usuario;
import com.speedmedical.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/usuario/create")
    public String showCreateForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario_create";
    }

    @PostMapping("/usuario/create")
    public String createUser(Usuario usuario, Model model) {
        try {
            // CORREÇÃO: Usamos o método correto 'salvarUsuario'
            // Como ele é void, apenas chamamos ele. Se der erro, cai no catch.
            usuarioService.salvarUsuario(usuario);
            
            return "redirect:/login?success";
            
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao cadastrar usuário: " + e.getMessage());
            return "usuario_create";
        }
    }
}