package com.vidaplus.config;

import com.vidaplus.entity.Usuario;
import com.vidaplus.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        System.out.println(">>> [HANDLER SUCESSO] Usuário autenticado: " + authentication.getName());

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        System.out.println(">>> [HANDLER SUCESSO] Perfis encontrados: " + roles);

        // --- REGISTRO DE LOG ---
        try {
            Usuario usuario = usuarioRepository.findByUsernameOrCpf(authentication.getName());
            if(usuario != null) {
                System.out.println(">>> [LOGIN] ID: " + usuario.getId() + " - Nome: " + usuario.getNome());
                
                // Log específico para Motorista para debug
                if (roles.contains("MOTORISTA")) {
                    System.out.println(">>> [DEBUG] Motorista detectado no Login 1. Redirecionando para Home para seguir ao Login 2.");
                }
            }
        } catch (Exception e) {
            System.err.println(">>> [AVISO] Não foi possível carregar detalhes do usuário no log.");
        }

        // --- MANUTENÇÃO DA REGRA: REDIRECIONAMENTO PADRÃO ---
        // Conforme seu projeto: Todo mundo vai para a HOME após o primeiro login.
        
        System.out.println(">>> Redirecionando todos os perfis para /home");
        response.sendRedirect("/home");
    }
}