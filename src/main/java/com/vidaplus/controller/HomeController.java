package com.vidaplus.controller;

import com.vidaplus.entity.Usuario;
import com.vidaplus.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    @Autowired private UsuarioRepository usuarioRepository;

    // Redireciona a raiz para o login
    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        // 1. Verificação de Segurança
        if (principal == null) return "redirect:/login";

        // 2. Carrega apenas o Usuário (para exibir "Olá, Nome")
        Usuario usuario = usuarioRepository.findByUsernameOrCpf(principal.getName());
        model.addAttribute("usuario", usuario);

        // OBS: Removemos os carregamentos de listas (Agendamentos, Prontuários, Triagem)
        // pois a Home agora é apenas um Dashboard de ícones. 
        // Esses dados agora são carregados exclusivamente em /pacientes.

        // 3. Retorna a view correta (dentro da pasta pages)
        return "pages/home"; 
    }
}