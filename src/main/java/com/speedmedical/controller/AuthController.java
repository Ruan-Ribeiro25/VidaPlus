package com.speedmedical.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.speedmedical.entity.Usuario;
import com.speedmedical.service.LogService;
import com.speedmedical.service.UsuarioService;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private LogService logService; 

    // --- CADASTRO ---
    @GetMapping("/register")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register"; // Procura templates/register.html
    }

    @PostMapping("/register")
    public String cadastrarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            if (usuario.getUsername() == null || usuario.getUsername().isEmpty()) {
                usuario.setUsername(usuario.getEmail());
            }
            usuarioService.salvarUsuario(usuario);
            
            // Log de Segurança
            logService.salvarLog(usuario, "CADASTRO_INICIAL", "Pré-cadastro realizado: " + usuario.getUsername());
            
            redirectAttributes.addFlashAttribute("mensagem", "Cadastro realizado! Verifique seu e-mail.");
            return "redirect:/verificar-conta";
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Erro: Este CPF ou Login já possui cadastro.");
            return "redirect:/register";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ocorreu um erro no sistema.");
            return "redirect:/register";
        }
    }

    // --- VERIFICAÇÃO ---
    @GetMapping("/verificar-conta")
    public String exibirVerificacao() {
        return "verificar-conta"; // Procura templates/verificar-conta.html
    }

    @PostMapping("/verificar-conta")
    public String verificarCodigo(@RequestParam("codigo") String codigo, RedirectAttributes redirectAttributes) {
        boolean verificado = usuarioService.verificarConta(codigo);
        if (verificado) {
            redirectAttributes.addFlashAttribute("logout", "Conta ativada! Faça login.");
            return "redirect:/login";
        }
        return "redirect:/verificar-conta?error";
    }
    
    // --- LOGIN ---
    @GetMapping("/login")
    public String login() {
        return "login"; // Procura templates/login.html
    }
}