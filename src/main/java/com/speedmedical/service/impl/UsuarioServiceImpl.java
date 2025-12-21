package com.speedmedical.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.speedmedical.entity.Polo;
import com.speedmedical.entity.Usuario;
import com.speedmedical.repository.PoloRepository;
import com.speedmedical.repository.UsuarioRepository;
import com.speedmedical.service.EmailService;
import com.speedmedical.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PoloRepository poloRepository; // Necessário para achar o Polo pelo CEP

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public void salvarUsuario(Usuario usuario) {
        // 1. Criptografia e Configuração Básica
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRole("ROLE_USER"); // Padrão
        
        // Gera código de 6 dígitos para o e-mail
        String codigo = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        usuario.setCodigoVerificacao(codigo);
        usuario.setEnabled(false);

        // 2. LÓGICA DE POLO AUTOMÁTICO (Busca pelo CEP)
        if (usuario.getCep() != null) {
            Polo poloEncontrado = poloRepository.findByCep(usuario.getCep());
            
            // Se não achar pelo CEP exato, tenta pela Cidade
            if (poloEncontrado == null && usuario.getCidade() != null) {
                poloEncontrado = poloRepository.findByCidade(usuario.getCidade());
            }
            
            // Se encontrou, vincula o usuário a este Polo
            if (poloEncontrado != null) {
                usuario.adicionarPolo(poloEncontrado);
            }
        }

        // 3. Salva no Banco e Envia E-mail
        usuarioRepository.save(usuario);
        emailService.enviarEmailConfirmacao(usuario.getEmail(), usuario.getNome(), codigo);
    }

    @Override
    public boolean verificarConta(String codigo) {
        Usuario usuario = usuarioRepository.findByCodigoVerificacao(codigo);
        if (usuario == null || usuario.isEnabled()) {
            return false;
        } else {
            usuario.setCodigoVerificacao(null);
            usuario.setEnabled(true);
            usuarioRepository.save(usuario);
            return true;
        }
    }

    @Override
    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    // --- IMPLEMENTAÇÃO DOS NOVOS MÉTODOS PARA OS ERROS DO CONTROLLER ---

    @Override
    public List<Usuario> listarPorRole(String role) {
        return usuarioRepository.findByRole(role);
    }

    @Override
    public List<Usuario> listarPorRoleEPolo(String role, Long poloId) {
        return usuarioRepository.findByPoloIdAndRole(poloId, role);
    }

    @Override
    public void verificarEAtualizarPolo(Usuario usuario, String novoCep) {
        Polo novoPolo = poloRepository.findByCep(novoCep);
        if (novoPolo != null) {
            // Verifica se já possui o polo para não duplicar
            boolean jaTem = usuario.getPolos().stream()
                    .anyMatch(p -> p.getId().equals(novoPolo.getId()));
            
            if (!jaTem) {
                usuario.adicionarPolo(novoPolo);
                usuarioRepository.save(usuario);
            }
        }
    }

    @Override
    public List<Usuario> listarParaDashboard(String roleAlvo, Usuario usuarioLogado) {
        // Se for Admin Geral, retorna tudo
        if ("ROLE_ADMIN_GERAL".equals(usuarioLogado.getRole())) {
            return usuarioRepository.findByRole(roleAlvo);
        }
        
        // Se for Profissional/Admin Local, retorna só do seu Polo
        if (!usuarioLogado.getPolos().isEmpty()) {
            Polo poloAtual = usuarioLogado.getPolos().iterator().next();
            return usuarioRepository.findByPoloIdAndRole(poloAtual.getId(), roleAlvo);
        }
        
        return List.of(); // Retorna lista vazia se não tiver permissão/polo
    }
}