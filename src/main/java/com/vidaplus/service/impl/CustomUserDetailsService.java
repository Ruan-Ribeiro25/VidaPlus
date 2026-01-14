package com.vidaplus.service.impl;

import com.vidaplus.entity.Usuario;
import com.vidaplus.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        System.out.println(">>> [LOGIN] Tentativa de login com: " + login);

        // Tenta buscar por Username ou CPF (Padrão)
        Usuario usuario = usuarioRepository.findByUsernameOrCpf(login);
        
        // Se não achou, tenta buscar pelo E-mail (Caso o repositório tenha esse método, senão ele vai dar erro aqui, mas vamos manter o padrão por enquanto)
        if (usuario == null) {
             System.out.println(">>> [LOGIN] Usuário não encontrado por Username/CPF. Verifique se o Repositório busca por Email.");
             // Se você tiver um método findByEmail, usaria aqui. Por enquanto, assumimos que o login falhou.
             throw new UsernameNotFoundException("Usuário não encontrado: " + login);
        }

        System.out.println(">>> [LOGIN] Usuário encontrado: " + usuario.getEmail());
        System.out.println(">>> [LOGIN] ID: " + usuario.getId());
        System.out.println(">>> [LOGIN] Ativo: " + usuario.isAtivo());
        System.out.println(">>> [LOGIN] Senha Hash no Banco: " + usuario.getSenha());

        // Defesa contra senha nula
        String senhaParaLogin = (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) 
                                ? usuario.getSenha() 
                                : usuario.getPassword();

        if (senhaParaLogin == null || senhaParaLogin.isEmpty()) {
            System.out.println(">>> [ERRO CRÍTICO] Senha está NULA no banco de dados!");
            throw new UsernameNotFoundException("Erro de dados: Senha vazia.");
        }

        // Ajuste de Perfil: Se o SecurityConfig pede "PACIENTE", não adicione "ROLE_"
        String perfil = (usuario.getPerfil() != null) ? usuario.getPerfil() : "PACIENTE";
        
        // Se no banco estiver apenas "PACIENTE", mantemos assim para bater com o SecurityConfig
        System.out.println(">>> [LOGIN] Perfil carregado: " + perfil);

        return new User(
                usuario.getUsername(), // O identificador principal
                senhaParaLogin,        // A senha criptografada
                usuario.isAtivo(),     // Enabled (Se false, login falha)
                true,                  // Account Non Expired
                true,                  // Credentials Non Expired
                true,                  // Account Non Locked
                Collections.singleton(new SimpleGrantedAuthority(perfil))
        );
    }
}