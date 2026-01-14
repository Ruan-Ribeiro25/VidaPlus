package com.vidaplus.controller;

import com.vidaplus.entity.Agendamento;
import com.vidaplus.entity.Usuario;
import com.vidaplus.repository.AgendamentoRepository;
import com.vidaplus.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Controller
public class AgendamentoController {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Lista oficial igual ao cadastro profissional
    private final List<String> ESPECIALIDADES = Arrays.asList(
        "Anestesiologia", "Cardiologia", "Cirurgia Geral", "Clínica Médica",
        "Dermatologia", "Endocrinologia", "Gastroenterologia", "Ginecologia e Obstetrícia",
        "Neurologia", "Oftalmologia", "Ortopedia e Traumatologia", "Otorrinolaringologia",
        "Pediatria", "Psiquiatria", "Radiologia", "Triagem", "Urologia", "UTI Adulto", "UTI Pediátrica"
    );

    // Lista de Feriados Fixos (Dia-Mês)
    private final List<String> FERIADOS = Arrays.asList(
        "01-01", "21-04", "01-05", "07-09", "12-10", "02-11", "15-11", "25-12"
    );

    @GetMapping("/agendamentos")
    public String listarAgendamentos(Model model, Principal principal) {
        if(principal == null) return "redirect:/login";

        String loginUsuario = principal.getName();
        Usuario usuario = usuarioRepository.findByUsernameOrCpf(loginUsuario);
        
        model.addAttribute("usuario", usuario);
        List<Agendamento> lista = agendamentoRepository.findByUsuario(usuario);
        model.addAttribute("agendamentos", lista);
        return "agendamento/agendamentos"; 
    }

    // --- FORMULÁRIO DE AGENDAMENTO ---
    @GetMapping("/agendamentos/novo")
    public String novoAgendamento(Model model) {
        model.addAttribute("agendamento", new Agendamento());
        model.addAttribute("listaEspecialidades", ESPECIALIDADES); // Envia lista para o HTML
        return "agendamento/agendamento-form";
    }

    // --- IMPORTANTE: O método da API (/api/profissionais...) foi REMOVIDO daqui 
    // pois foi movido para o ApiController.java para evitar conflito de URL. ---

    // --- SALVAR COM VALIDAÇÕES DE HORÁRIO E REGRAS ---
    @PostMapping("/agendamentos/salvar")
    public String salvarAgendamento(Agendamento agendamento, 
                                    Principal principal,
                                    @RequestParam(required = false) Long profissionalId,
                                    RedirectAttributes redirectAttributes) { 
        
        String loginUsuario = principal.getName();
        Usuario usuario = usuarioRepository.findByUsernameOrCpf(loginUsuario);
        
        agendamento.setUsuario(usuario);
        agendamento.setStatus("Agendado"); 
        
        // 1. Define o nome do Profissional baseado no ID do Select
        String nomeMedico = "Profissional Geral";
        if (profissionalId != null) {
            Usuario medico = usuarioRepository.findById(profissionalId).orElse(null);
            if (medico != null) {
                nomeMedico = "Dr(a). " + medico.getNome();
            }
        }
        agendamento.setProfissional(nomeMedico);
        
        // --- INÍCIO DAS VALIDAÇÕES DE REGRA DE NEGÓCIO ---
        LocalDateTime dataHora = agendamento.getDataHora();
        if (dataHora == null) dataHora = LocalDateTime.now().plusDays(1);

        // A. Data no Passado
        if (dataHora.isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("erroAgendamento", "Não é possível agendar em uma data retroativa.");
            return "redirect:/agendamentos/novo";
        }

        // B. Fim de Semana (Sábado ou Domingo)
        DayOfWeek diaSemana = dataHora.getDayOfWeek();
        if (diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY) {
            redirectAttributes.addFlashAttribute("erroAgendamento", "Atendimentos apenas de Segunda a Sexta-feira.");
            return "redirect:/agendamentos/novo";
        }

        // C. Horário Comercial (08:00 as 18:00)
        LocalTime hora = dataHora.toLocalTime();
        if (hora.isBefore(LocalTime.of(8, 0)) || hora.isAfter(LocalTime.of(18, 0))) {
            redirectAttributes.addFlashAttribute("erroAgendamento", "Horário de atendimento apenas entre 08:00 e 18:00.");
            return "redirect:/agendamentos/novo";
        }

        // D. Feriados Nacionais
        String diaMes = String.format("%02d-%02d", dataHora.getDayOfMonth(), dataHora.getMonthValue());
        if (FERIADOS.contains(diaMes)) {
            redirectAttributes.addFlashAttribute("erroAgendamento", "Não há atendimento em feriados nacionais.");
            return "redirect:/agendamentos/novo";
        }

        // E. Conflito de Agenda (Médico já ocupado)
        // Verifica se existe agendamento para ESSE médico, NESSA hora, que NÃO esteja Cancelado
        boolean ocupado = agendamentoRepository.existsByProfissionalAndDataHoraAndStatusNot(nomeMedico, dataHora, "Cancelado");
        
        if (ocupado) {
            redirectAttributes.addFlashAttribute("erroAgendamento", "Este profissional já possui um agendamento neste horário. Por favor, escolha outro.");
            return "redirect:/agendamentos/novo";
        }

        // --- FIM DAS VALIDAÇÕES ---

        agendamentoRepository.save(agendamento);
        return "redirect:/agendamentos?sucesso=true"; 
    }
}