package com.speedmedical.controller;

import com.speedmedical.entity.Usuario;
import com.speedmedical.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SecurityCheckController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Abre a tela de "Segundo Login"
    @GetMapping("/auth/bloqueio")
    public String telaBloqueio(@RequestParam("destino") String destino, Model model) {
        model.addAttribute("destino", destino);
        return "login-secundario";
    }

    // 2. Valida a senha digitada
    @PostMapping("/auth/validar-segundo-passo")
    public String validarSenha(@RequestParam("senhaConfirmacao") String senha, 
                               @RequestParam("destino") String destino,
                               HttpSession session,
                               Model model) {
        
        // Pega quem já está logado no sistema
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.findByUsername(username);

        // Compara a senha digitada com a senha real do banco
        if (passwordEncoder.matches(senha, usuario.getPassword())) {
            
            // SE A SENHA TIVER CORRETA:
            // Cria uma "chave" na sessão dizendo que ele passou no segundo login
            if (destino.contains("admin")) {
                session.setAttribute("ADMIN_LIBERADO", true);
            } else if (destino.contains("profissional")) {
                session.setAttribute("PROFISSIONAL_LIBERADO", true);
            }
            
            return "redirect:" + destino; // Manda para o painel desejado
        } else {
            // SE A SENHA TIVER ERRADA:
            model.addAttribute("erro", "Senha incorreta.");
            model.addAttribute("destino", destino);
            return "login-secundario";
        }
    }
}