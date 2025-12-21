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
import java.util.List;

@Controller
public class PacienteController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    // --- 1. DASHBOARD = PRONTUÁRIO (Perfil + Histórico) ---
    @GetMapping("/pacientes")
    public String dashboardPaciente(Model model, Principal principal) {
        String loginUsuario = principal.getName();
        Usuario usuario = usuarioRepository.findByUsernameOrCpf(loginUsuario, loginUsuario);
        
        model.addAttribute("usuario", usuario);
        // O HTML usa "paciente" para os dados do perfil
        model.addAttribute("paciente", usuario);

        // BUSCA O HISTÓRICO REAL (Essencial para a tabela da direita)
        List<Agendamento> agendamentos = agendamentoRepository.findByUsuario(usuario);

        // Se vazio, cria exemplo para não ficar em branco
        if (agendamentos.isEmpty()) {
            Agendamento exemplo = new Agendamento("Cadastro Inicial", "Sistema", LocalDateTime.now(), "Concluído", usuario);
            agendamentoRepository.save(exemplo);
            agendamentos.add(exemplo);
        }

        model.addAttribute("historico", agendamentos);
        
        return "paciente/pacientes"; 
    }

    // --- 2. TELA DE EDITAR DADOS ---
    @GetMapping("/pacientes/editar")
    public String editarDados(Model model, Principal principal) {
        String loginUsuario = principal.getName();
        Usuario usuario = usuarioRepository.findByUsernameOrCpf(loginUsuario, loginUsuario);
        
        model.addAttribute("usuario", usuario); 
        return "paciente-editar"; // Esse arquivo fica na raiz de templates ou pasta, ajuste se necessário
    }

    // --- 3. SALVAR DADOS ---
    @PostMapping("/pacientes/salvar-dados")
    public String salvarDados(Usuario usuarioForm, Principal principal) {
        String loginUsuario = principal.getName();
        Usuario usuarioBanco = usuarioRepository.findByUsernameOrCpf(loginUsuario, loginUsuario);

        usuarioBanco.setTelefone(usuarioForm.getTelefone());
        usuarioBanco.setCep(usuarioForm.getCep());
        usuarioBanco.setLogradouro(usuarioForm.getLogradouro());
        usuarioBanco.setNumero(usuarioForm.getNumero());
        usuarioBanco.setBairro(usuarioForm.getBairro());
        usuarioBanco.setCidade(usuarioForm.getCidade());
        usuarioBanco.setUf(usuarioForm.getUf());

        usuarioRepository.save(usuarioBanco);

        return "redirect:/pacientes";
    }
}