package com.speedmedical.controller;

import com.speedmedical.entity.Agendamento;
import com.speedmedical.entity.Documento;
import com.speedmedical.entity.Usuario;
import com.speedmedical.repository.AgendamentoRepository;
import com.speedmedical.repository.DocumentoRepository;
import com.speedmedical.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class DocumentoController {

    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @GetMapping("/documentos")
    public String listarDocumentos(Model model, Principal principal) {
        String loginUsuario = principal.getName();
        Usuario usuario = usuarioRepository.findByUsernameOrCpf(loginUsuario, loginUsuario);
        model.addAttribute("usuario", usuario);
        
        List<Documento> docs = documentoRepository.findByUsuario(usuario);

        // Gera dados falsos se estiver vazio (apenas para teste visual)
        if (docs.isEmpty()) {
            criarDadosDeExemplo(usuario);
            docs = documentoRepository.findByUsuario(usuario); 
        }

        // Agrupa por consulta
        Map<String, List<Documento>> documentosAgrupados = new LinkedHashMap<>();
        for (Documento doc : docs) {
            String chaveGrupo;
            if (doc.getAgendamento() != null) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                chaveGrupo = doc.getAgendamento().getProcedimento() + " - " + 
                             doc.getAgendamento().getProfissional() + " (" + 
                             doc.getAgendamento().getDataHora().format(fmt) + ")";
            } else {
                chaveGrupo = "Documentos Gerais";
            }
            documentosAgrupados.computeIfAbsent(chaveGrupo, k -> new ArrayList<>()).add(doc);
        }

        model.addAttribute("documentosMap", documentosAgrupados);

        // CORREÇÃO: O arquivo está em templates/documento/documentos.html
        return "documento/documentos"; 
    }

    @GetMapping("/documentos/download/{id}")
    public ResponseEntity<byte[]> downloadDocumento(@PathVariable Long id) {
        Documento doc = documentoRepository.findById(id).orElse(null);
        if (doc == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getNomeArquivo() + "\"")
                .contentType(MediaType.parseMediaType(doc.getTipoArquivo()))
                .body(doc.getDados());
    }

    private void criarDadosDeExemplo(Usuario usuario) {
        Agendamento consulta1 = new Agendamento("Cardiologista", "Dr. House", LocalDateTime.now().minusDays(10), "Concluído", usuario);
        agendamentoRepository.save(consulta1);

        byte[] dummyContent = "Conteudo de teste".getBytes();
        Documento doc1 = new Documento("Receita_Medica.pdf", "application/pdf", dummyContent, usuario, consulta1);
        Documento doc2 = new Documento("Eletrocardiograma.png", "image/png", dummyContent, usuario, consulta1);
        
        documentoRepository.saveAll(List.of(doc1, doc2));
    }
}