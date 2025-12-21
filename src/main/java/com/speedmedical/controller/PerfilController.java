package com.speedmedical.controller;

import com.speedmedical.entity.*;
import com.speedmedical.repository.*;
import com.speedmedical.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private ProfissionalRepository profissionalRepository;
    
    @Autowired
    private AdministradorRepository administradorRepository;

    // 1. Tela de Completar (Redireciona para o form certo)
    @GetMapping("/perfil/completar")
    public String exibirFormularioCompletar(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.findByUsername(username);
        
        if (usuario.getRole().equals("ROLE_PROFISSIONAL")) {
            return "perfil/completar-profissional";
        } else if (usuario.getRole().contains("ADMIN")) {
            return "perfil/completar-admin";
        }
        return "redirect:/home";
    }

    // 2. Salvar Profissional (EXIGÊNCIA: CRM OBRIGATÓRIO + STATUS PENDENTE)
    @PostMapping("/perfil/salvar-profissional")
    public String salvarProfissional(@RequestParam("crm") String crm,
                                     @RequestParam("matricula") String matricula,
                                     @RequestParam("atuacao") String atuacao) {
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.findByUsername(username);

        Profissional prof = new Profissional();
        prof.setUsuario(usuario);
        prof.setCrm(crm); // Exigido
        prof.setMatricula(matricula);
        prof.setAtuacao(atuacao);
        
        // --- AQUI ESTÁ A REGRA: ---
        prof.setStatusAprovacao("PENDENTE"); 
        // --------------------------

        profissionalRepository.save(prof);
        
        // Redireciona para o painel (o DashboardController vai barrar e mostrar a tela de espera)
        return "redirect:/profissional/painel"; 
    }

    // 3. Salvar Admin (EXIGÊNCIA: SEM CRM, APENAS MATRÍCULA)
    @PostMapping("/perfil/salvar-admin")
    public String salvarAdmin(@RequestParam("matricula") String matricula) {
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.findByUsername(username);

        Administrador admin = new Administrador();
        admin.setUsuario(usuario);
        admin.setMatricula(matricula);
        // Admin se auto-aprova ou nasce aprovado (conforme sua regra, admins são confiáveis ou criados pelo ROOT)
        admin.setNivelAcesso("GERENTE"); 

        administradorRepository.save(admin);
        
        return "redirect:/admin/painel";
    }
}