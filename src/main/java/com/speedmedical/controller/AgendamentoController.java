package com.speedmedical.controller;

import com.speedmedical.entity.Agendamento;
import com.speedmedical.entity.Usuario;
import com.speedmedical.repository.AgendamentoRepository;
import com.speedmedical.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class AgendamentoController {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- 1. DASHBOARD DE AGENDAMENTOS (NOVO) ---
    @GetMapping("/agendamentos")
    public String listarAgendamentos(Model model, Principal principal) {
        String loginUsuario = principal.getName();
        Usuario usuario = usuarioRepository.findByUsernameOrCpf(loginUsuario, loginUsuario);
        
        // Dados para a Navbar
        model.addAttribute("usuario", usuario);

        // Busca lista completa
        List<Agendamento> lista = agendamentoRepository.findByUsuario(usuario);
        model.addAttribute("agendamentos", lista);

        return "agendamentos"; // Nome do HTML que vamos criar
    }

    // --- 2. FORMULÁRIO DE NOVO AGENDAMENTO ---
    @GetMapping("/agendamentos/novo")
    public String novoAgendamento(Model model) {
        model.addAttribute("agendamento", new Agendamento());
        return "agendamento-form";
    }

    // --- 3. SALVAR COM REGRA DE CONFLITO ---
    @PostMapping("/agendamentos/salvar")
    public String salvarAgendamento(Agendamento agendamento, Principal principal, Model model) {
        if (agendamento.getDataHora() == null) {
            agendamento.setDataHora(LocalDateTime.now().plusDays(1).withNano(0));
        }

        String nomeProfissional = agendamento.getProfissional();
        LocalDateTime horarioSolicitado = agendamento.getDataHora();

        // Verifica colisão
        boolean ocupado = agendamentoRepository.existsByProfissionalAndDataHoraAndStatusNot(
                nomeProfissional, horarioSolicitado, "Cancelado");

        if (ocupado) {
            LocalDateTime proximoLivre = horarioSolicitado;
            while (agendamentoRepository.existsByProfissionalAndDataHoraAndStatusNot(nomeProfissional, proximoLivre, "Cancelado")) {
                proximoLivre = proximoLivre.plusMinutes(30);
            }
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
            
            model.addAttribute("erro", "Horário indisponível.");
            model.addAttribute("sugestao", "Tente: " + proximoLivre.format(fmt));
            model.addAttribute("agendamento", agendamento);
            return "agendamento-form";
        }

        String loginUsuario = principal.getName();
        Usuario usuario = usuarioRepository.findByUsernameOrCpf(loginUsuario, loginUsuario);
        
        agendamento.setUsuario(usuario);
        agendamento.setStatus("Agendado");
        
        agendamentoRepository.save(agendamento);
        
        return "redirect:/agendamentos"; // Redireciona para o novo Dashboard
    }
}