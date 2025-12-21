package com.speedmedical.service.impl;

import com.speedmedical.entity.Usuario;
import com.speedmedical.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        
        // Lógica Híbrida: Busca pelo Username (ex: "Ruan") OU pelo CPF (ex: "107...")
        // Isso garante que o usuário consiga entrar de qualquer jeito.
        Usuario usuario = usuarioRepository.findByUsernameOrCpf(login, login);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário ou CPF não encontrado!");
        }
        
        if (!usuario.isEnabled()) {
            throw new UsernameNotFoundException("Conta não ativada. Verifique seu e-mail.");
        }

        // Retorna o objeto User do Spring Security
        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRole().replace("ROLE_", ""))
                .build();
    }
}