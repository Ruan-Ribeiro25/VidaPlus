package com.speedmedical.controller;

import com.speedmedical.entity.Administrador;
import com.speedmedical.entity.Profissional;
import com.speedmedical.entity.Usuario;
import com.speedmedical.repository.AdministradorRepository;
import com.speedmedical.repository.DocumentoRepository;
import com.speedmedical.repository.ProfissionalRepository;
import com.speedmedical.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    // =======================================================
    // 1. ÁREA DO PACIENTE (Acesso Direto)
    // =======================================================
    @GetMapping("/paciente/dashboard")
    public String dashboardPaciente(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario paciente = usuarioService.findByUsername(username);
        
        model.addAttribute("usuario", paciente);
        
        // --- CORREÇÃO AQUI (Linha 44) ---
        // Usamos findByUsuario passando o objeto paciente inteiro
        model.addAttribute("documentos", documentoRepository.findByUsuario(paciente));
        
        return "paciente/dashboard";
    }

    // =======================================================
    // 2. DASHBOARD DO PROFISSIONAL (Acesso Controlado)
    // =======================================================
    @GetMapping("/profissional/painel")
    public String painelProfissional(Model model, HttpSession session) {
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.findByUsername(username);

        // A. Verifica se completou o cadastro (CRM, etc)
        Profissional perfil = profissionalRepository.findByUsuarioId(usuario.getId());
        if (perfil == null) {
            return "redirect:/perfil/completar";
        }

        // B. Verifica se o cadastro foi aprovado pelo Admin
        if ("PENDENTE".equals(perfil.getStatusAprovacao())) {
            return "perfil/aguardando-aprovacao"; // Tela de aviso
        }

        // C. Verifica o Segundo Login (Senha extra)
        if (session.getAttribute("PROFISSIONAL_LIBERADO") == null) {
            return "redirect:/auth/bloqueio?destino=/profissional/painel";
        }

        // D. Acesso Liberado
        // Mostra pacientes do polo dele (ou todos se for geral)
        model.addAttribute("pacientes", usuarioService.listarParaDashboard("ROLE_USER", usuario));
        
        return "profissional/painel";
    }

    // =======================================================
    // 3. DASHBOARD DO ADMIN (Acesso Controlado)
    // =======================================================
    @GetMapping("/admin/painel")
    public String painelAdmin(Model model, HttpSession session) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.findByUsername(username);

        // A. Verifica se completou o cadastro (Matrícula)
        Administrador perfil = administradorRepository.findByUsuarioId(usuario.getId());
        if (perfil == null) {
            return "redirect:/perfil/completar";
        }

        // B. Verifica o Segundo Login
        if (session.getAttribute("ADMIN_LIBERADO") == null) {
            return "redirect:/auth/bloqueio?destino=/admin/painel";
        }

        // C. Acesso Liberado
        List<Profissional> listaProfissionais = profissionalRepository.findAll();
        model.addAttribute("listaProfissionaisCompleta", listaProfissionais);
        
        // Carrega pacientes normalmente
        model.addAttribute("pacientes", usuarioService.listarParaDashboard("ROLE_USER", usuario));
        
        return "admin/painel";
    }

    // =======================================================
    // 4. AÇÃO DE APROVAÇÃO (ADMIN)
    // =======================================================
    @GetMapping("/admin/aprovar/{id}")
    public String aprovarProfissional(@PathVariable("id") Long idProfissional) {
        
        Profissional prof = profissionalRepository.findById(idProfissional).orElse(null);
        
        if (prof != null) {
            prof.setStatusAprovacao("APROVADO");
            profissionalRepository.save(prof);
        }
        
        return "redirect:/admin/painel";
    }
}