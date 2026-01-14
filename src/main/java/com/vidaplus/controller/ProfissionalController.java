package com.vidaplus.controller;

import com.vidaplus.entity.*;
import com.vidaplus.repository.*;
import com.vidaplus.util.GeoLocationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; 

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Arrays; 
import java.text.Normalizer;
import java.util.Objects; 
import java.util.stream.Collectors;

@Controller
@RequestMapping("/profissional")
public class ProfissionalController {
	
    @Autowired private AmbulanciaRepository ambulanciaRepository; 
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ProntuarioRepository prontuarioRepository;
    @Autowired private AgendamentoRepository agendamentoRepository;
    @Autowired private DocumentoRepository documentoRepository;
    @Autowired private GeoLocationUtil geoLocationUtil;
    @Autowired private SinaisVitaisRepository sinaisVitaisRepository;
    @Autowired private ProfissionalRepository profissionalRepository;

    @GetMapping("/painel")
    public String painel(Model model, Principal principal, 
                         @RequestParam(value = "busca", required = false) String busca,
                         @RequestParam(value = "idPaciente", required = false) Long idPaciente) {
        
        if (principal == null) return "redirect:/login"; 

        Usuario usuarioLogado = usuarioRepository.findByUsernameOrCpf(principal.getName());
        if (usuarioLogado == null) return "redirect:/login";

        // 1. Carrega dados do Profissional
        Profissional profissionalEntity = profissionalRepository.findAll().stream()
            .filter(p -> p.getUsuario() != null && Objects.equals(p.getUsuario().getId(), usuarioLogado.getId()))
            .findFirst()
            .orElse(null);
        
        if (profissionalEntity == null) {
            profissionalEntity = new Profissional();
            profissionalEntity.setUsuario(usuarioLogado);
            profissionalEntity.setStatusAprovacao("PENDENTE");
        }
        model.addAttribute("profissional", profissionalEntity); 
        model.addAttribute("usuario", usuarioLogado);

        // 2. Flags de Perfil
        String perfil = usuarioLogado.getPerfil() != null ? usuarioLogado.getPerfil().toUpperCase() : "";
        perfil = Normalizer.normalize(perfil, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");

        boolean isMedico = perfil.contains("MEDICO");
        boolean isEnfermeiro = perfil.contains("ENFERMEIRO");
        boolean isMotorista = perfil.contains("MOTORISTA");

        model.addAttribute("isMedico", isMedico);
        model.addAttribute("isEnfermeiro", isEnfermeiro);
        model.addAttribute("isMotorista", isMotorista);

        // 3. DIRECIONAMENTO
        if (perfil.contains("MOTORISTA")) {
            return carregarPainelMotorista(model, usuarioLogado);
        } 
        else if (perfil.contains("MEDICO")) {
            return carregarPainelMedico(model, usuarioLogado, busca, idPaciente);
        }
        else if (perfil.contains("ENFERMEIRO")) {
            return carregarPainelEnfermeiro(model, usuarioLogado, busca, idPaciente);
        }
        else if (perfil.contains("RECEPCAO") || perfil.contains("RECEPCIONISTA")) {
            return carregarPainelRecepcao(model, usuarioLogado, busca);
        }
        else if (perfil.contains("TECNICO")) {
            return carregarPainelTecnico(model, usuarioLogado, busca, idPaciente);
        }
        else if (perfil.contains("SERVICOS") || perfil.contains("LIMPEZA")) {
            return carregarPainelServicos(model, usuarioLogado);
        }

        return "profissional/painel-padrao";
    }

    // =========================================================================
    // MÉTODOS DE CARREGAMENTO (COM CORREÇÃO DE NULL POINTER)
    // =========================================================================

    private String carregarPainelMotorista(Model model, Usuario motorista) {
        String nomeMotorista = motorista.getNome();
        Ambulancia meuCarro = ambulanciaRepository.findAll().stream()
            .filter(a -> a.getMotorista() != null && a.getMotorista().equals(nomeMotorista))
            .findFirst()
            .orElse(null);

        if (meuCarro != null) {
            model.addAttribute("meuCarro", meuCarro);
        } else {
            List<Ambulancia> disponiveis = ambulanciaRepository.findAll().stream()
                .filter(a -> "DISPONIVEL".equals(a.getStatus()) && (a.getMotorista() == null || "-".equals(a.getMotorista())))
                .collect(Collectors.toList());
            model.addAttribute("ambulanciasDisponiveis", disponiveis);
        }
        return "profissional/painel-motorista"; 
    }

    private String carregarPainelMedico(Model model, Usuario medico, String busca, Long idPaciente) {
        carregarPacientes(model, medico, busca);
        carregarDadosClinicos(model, idPaciente); // Agora seguro contra null
        
        // Mocks Gráficos Médico
        model.addAttribute("graficoMeses", Arrays.asList("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"));
        model.addAttribute("graficoAtendimentos", Arrays.asList(45, 52, 38, 60, 55, 70, 65, 80, 75, 60, 0, 0));
        model.addAttribute("graficoCancelamentos", Arrays.asList(2, 5, 1, 4, 3, 5, 2, 4, 1, 2, 0, 0));
        model.addAttribute("graficoPlantoes", Arrays.asList(10, 12, 8, 15, 12, 14, 14, 16, 15, 12, 0, 0));
        model.addAttribute("graficoSatisfacao", Arrays.asList(4.8, 4.5, 4.9, 4.2, 4.7, 4.6, 4.8, 4.5, 4.9, 4.8, 0.0, 0.0));
        model.addAttribute("kpiTotalPlantoes", 148);
        model.addAttribute("kpiTotalAtendimentos", 600);
        model.addAttribute("kpiMediaSatisfacao", "4.8");
        model.addAttribute("graficoAnos", Arrays.asList("2021", "2022", "2023", "2024", "2025"));
        model.addAttribute("graficoTotalAnual", Arrays.asList(420, 560, 610, 750, 680));

        return "profissional/painel-medico"; 
    }

    private String carregarPainelEnfermeiro(Model model, Usuario enfermeiro, String busca, Long idPaciente) {
        carregarPacientes(model, enfermeiro, busca);
        carregarDadosClinicos(model, idPaciente); // Agora seguro contra null
        
        // Mocks Gráficos Enfermagem
        model.addAttribute("graficoMeses", Arrays.asList("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"));
        model.addAttribute("graficoAtendimentos", Arrays.asList(120, 145, 110, 160, 135, 155, 170, 180, 165, 140, 0, 0));
        model.addAttribute("graficoEncaminhados", Arrays.asList(100, 120, 90, 140, 110, 130, 150, 160, 140, 120, 0, 0));
        model.addAttribute("graficoObservacao", Arrays.asList(20, 25, 20, 20, 25, 25, 20, 20, 25, 20, 0, 0));
        model.addAttribute("kpiTriagensHoje", 42);
        model.addAttribute("kpiEmObservacao", 5);
        model.addAttribute("kpiTotalMensal", 1375);
        model.addAttribute("kpiMediaTempo", "8 min"); 
        model.addAttribute("graficoAnos", Arrays.asList("2021", "2022", "2023", "2024", "2025"));
        model.addAttribute("graficoTotalAnual", Arrays.asList(1200, 1450, 1600, 1800, 1750));
        
        return "profissional/painel-enfermeiro"; 
    }

    private String carregarPainelRecepcao(Model model, Usuario recepcao, String busca) {
        carregarPacientes(model, recepcao, busca);
        return "profissional/painel-recepcao"; 
    }

    private String carregarPainelTecnico(Model model, Usuario tecnico, String busca, Long idPaciente) {
        carregarPacientes(model, tecnico, busca);
        carregarDadosClinicos(model, idPaciente); // Agora seguro contra null
        return "profissional/painel-tecnico"; 
    }

    private String carregarPainelServicos(Model model, Usuario servicos) {
        return "profissional/painel-servicos"; 
    }

    // --- UTILITÁRIOS ---

    private void carregarPacientes(Model model, Usuario profissional, String busca) {
        String cidade = profissional.getCidade();
        List<Usuario> todos = usuarioRepository.findAll();
        List<Usuario> pacientes = todos.stream()
            .filter(u -> "PACIENTE".equalsIgnoreCase(u.getPerfil()) && u.isAtivo())
            .filter(u -> cidade == null || (u.getCidade() != null && u.getCidade().equalsIgnoreCase(cidade)))
            .collect(Collectors.toList());

        if (busca != null && !busca.isEmpty()) {
            String termo = busca.toLowerCase();
            pacientes = pacientes.stream()
                .filter(p -> p.getNome().toLowerCase().contains(termo) || p.getCpf().contains(termo))
                .collect(Collectors.toList());
        }
        model.addAttribute("resultados", pacientes);
    }

    // AQUI ESTAVA O ERRO: Adicionei a verificação de null
    private void carregarDadosClinicos(Model model, Long idPaciente) {
        if (idPaciente != null) { // <--- CORREÇÃO CRÍTICA AQUI
            Usuario paciente = usuarioRepository.findById(idPaciente).orElse(null);
            if (paciente != null) {
                model.addAttribute("pacienteSelecionado", paciente);
                model.addAttribute("historicoClinico", prontuarioRepository.findByPacienteOrderByDataHoraDesc(paciente));
                model.addAttribute("historicoTriagem", sinaisVitaisRepository.findByPacienteOrderByDataHoraDesc(paciente));
                model.addAttribute("documentosPaciente", documentoRepository.findByUsuario(paciente));
                
                Ambulancia missaoAtiva = ambulanciaRepository.findAll().stream()
                    .filter(a -> ("SOLICITADO".equals(a.getStatus()) || "EM_CHAMADO".equals(a.getStatus())) 
                              && a.getPrevisaoLiberacao() != null 
                              && a.getPrevisaoLiberacao().contains(paciente.getNome()))
                    .findFirst()
                    .orElse(null);
                
                if (missaoAtiva != null) {
                    model.addAttribute("missaoAtiva", missaoAtiva);
                    model.addAttribute("tempoEstimado", "10-15 min");
                }
            }
        }
    }

    // --- AÇÕES (POST) ---

    @PostMapping("/assumir-viatura")
    public String assumirViatura(Principal principal, @RequestParam Long idAmbulancia) {
        Usuario motorista = usuarioRepository.findByUsernameOrCpf(principal.getName());
        Ambulancia amb = ambulanciaRepository.findById(idAmbulancia).orElse(null);
        if (amb != null && "DISPONIVEL".equals(amb.getStatus())) {
            amb.setMotorista(motorista.getNome()); 
            ambulanciaRepository.save(amb);
        }
        return "redirect:/profissional/painel";
    }

    @PostMapping("/aceitar-missao")
    public String aceitarMissao(@RequestParam Long idAmbulancia) {
        Ambulancia amb = ambulanciaRepository.findById(idAmbulancia).orElse(null);
        if (amb != null && "SOLICITADO".equals(amb.getStatus())) {
            amb.setStatus("EM_CHAMADO");
            ambulanciaRepository.save(amb);
        }
        return "redirect:/profissional/painel";
    }

    @PostMapping("/concluir-missao")
    public String concluirMissao(@RequestParam Long idAmbulancia) {
        Ambulancia amb = ambulanciaRepository.findById(idAmbulancia).orElse(null);
        if (amb != null) {
            amb.setStatus("DISPONIVEL");
            amb.setPrevisaoLiberacao("-");
            ambulanciaRepository.save(amb);
        }
        return "redirect:/profissional/painel";
    }

    @PostMapping("/liberar-viatura")
    public String liberarViatura(@RequestParam Long idAmbulancia) {
        Ambulancia amb = ambulanciaRepository.findById(idAmbulancia).orElse(null);
        if (amb != null) {
            amb.setMotorista("-"); 
            amb.setStatus("DISPONIVEL");
            amb.setPrevisaoLiberacao("-");
            ambulanciaRepository.save(amb);
        }
        return "redirect:/profissional/painel";
    }

    @PostMapping("/solicitar-ambulancia")
    public String solicitarAmbulancia(Principal principal, @RequestParam Long idPaciente, @RequestParam String destino) {
        Usuario medico = usuarioRepository.findByUsernameOrCpf(principal.getName());
        Usuario paciente = usuarioRepository.findById(idPaciente).orElse(null);
        Ambulancia disponivel = ambulanciaRepository.findAll().stream()
            .filter(a -> "DISPONIVEL".equals(a.getStatus()) && !"-".equals(a.getMotorista()))
            .findFirst()
            .orElse(null);

        if (disponivel != null && paciente != null) {
            disponivel.setStatus("SOLICITADO");
            String rua = medico.getLogradouro() != null ? medico.getLogradouro() : "Endereço da Unidade";
            String num = medico.getNumero() != null ? medico.getNumero() : "S/N";
            String bairro = medico.getBairro() != null ? medico.getBairro() : "";
            String cep = medico.getCep() != null ? medico.getCep() : "";
            String enderecoCompleto = rua + ", " + num + " - " + bairro + (cep.isEmpty() ? "" : " (" + cep + ")");
            String fichaMissao = paciente.getNome() + "|" + medico.getNome() + "|" + enderecoCompleto + "|" + destino;
            
            disponivel.setPrevisaoLiberacao(fichaMissao);
            ambulanciaRepository.save(disponivel);
            return "redirect:/profissional/painel?idPaciente=" + idPaciente + "&msg=ambulancia_solicitada";
        }
        return "redirect:/profissional/painel?idPaciente=" + idPaciente + "&error=sem_viaturas";
    }

    @PostMapping("/salvar-prontuario")
    public String salvarProntuario(Principal principal, @RequestParam Long idPaciente, @RequestParam String anotacoes, @RequestParam String diagnostico, @RequestParam(required = false) String prescricao) {
        Usuario medico = usuarioRepository.findByUsernameOrCpf(principal.getName());
        Usuario paciente = usuarioRepository.findById(idPaciente).orElse(null);
        if (paciente != null && medico != null) {
            Prontuario p = new Prontuario(); 
            p.setPaciente(paciente); p.setMedico(medico); p.setConteudo(anotacoes); 
            p.setDiagnostico(diagnostico); p.setPrescricao(prescricao); p.setDataHora(LocalDateTime.now()); 
            prontuarioRepository.save(p);
        }
        return "redirect:/profissional/painel?idPaciente=" + idPaciente;
    }

    @PostMapping("/salvar-triagem")
    public String salvarTriagem(Principal principal, @RequestParam Long idPaciente, @RequestParam Integer sistolica, @RequestParam Integer diastolica, @RequestParam Integer glicemia, @RequestParam Double temperatura, @RequestParam String queixa) {
        Usuario enfermeiro = usuarioRepository.findByUsernameOrCpf(principal.getName());
        Usuario paciente = usuarioRepository.findById(idPaciente).orElse(null);
        if (paciente != null) {
            SinaisVitais sv = new SinaisVitais(); 
            sv.setPaciente(paciente); sv.setResponsavel(enfermeiro); 
            sv.setPressaoSistolica(sistolica); sv.setPressaoDiastolica(diastolica); 
            sv.setGlicemia(glicemia); sv.setTemperatura(temperatura); sv.setQueixa(queixa); 
            sv.setDataHora(LocalDateTime.now()); 
            sinaisVitaisRepository.save(sv);
        }
        return "redirect:/profissional/painel?idPaciente=" + idPaciente;
    }

    @PostMapping("/upload-documento")
    public String uploadDocumento(Principal principal, @RequestParam("idPaciente") Long idPaciente, @RequestParam("descricao") String descricao, @RequestParam("arquivo") MultipartFile arquivo) {
        Usuario paciente = usuarioRepository.findById(idPaciente).orElse(null);
        if (paciente != null && !arquivo.isEmpty()) {
            try {
                Documento doc = new Documento(); 
                doc.setUsuario(paciente); doc.setDescricao(descricao); 
                doc.setNomeArquivo(arquivo.getOriginalFilename()); doc.setDataUpload(LocalDateTime.now()); 
                doc.setDados(arquivo.getBytes());
                String nome = arquivo.getOriginalFilename().toLowerCase();
                if (nome.endsWith(".pdf")) doc.setTipo("pdf"); else doc.setTipo("img");
                documentoRepository.save(doc);
            } catch (IOException e) { e.printStackTrace(); }
        }
        return "redirect:/profissional/painel?idPaciente=" + idPaciente;
    }
}