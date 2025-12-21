package com.speedmedical.service;

import com.speedmedical.entity.Usuario;
import java.util.List;

public interface UsuarioService {
    
    void salvarUsuario(Usuario usuario);
    
    boolean verificarConta(String codigo);
    
    Usuario findByUsername(String username);
    
    // --- MÉTODOS NOVOS NECESSÁRIOS PARA O DASHBOARD E POLOS ---
    
    // Lista usuários por tipo (ex: trazer todos os médicos)
    List<Usuario> listarPorRole(String role);
    
    // Lista usuários filtrando por Role E pelo Polo específico (para o médico local)
    List<Usuario> listarPorRoleEPolo(String role, Long poloId);
    
    // Verifica se o usuário mudou de cidade e adiciona o novo polo
    void verificarEAtualizarPolo(Usuario usuario, String novoCep);
    
    // Método inteligente que decide qual lista devolver (Geral ou Local)
    List<Usuario> listarParaDashboard(String roleAlvo, Usuario usuarioLogado);
}