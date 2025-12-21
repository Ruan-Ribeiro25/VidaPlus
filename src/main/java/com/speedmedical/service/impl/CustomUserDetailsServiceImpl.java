package com.speedmedical.service.impl;

import com.speedmedical.entity.Usuario;
import com.speedmedical.repository.UsuarioRepository;
import com.speedmedical.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        // 1. Busca o usuário pelo CPF
        Usuario usuario = usuarioRepository.findByCpf(cpf);
        
        // 2. Se não existir, lança erro
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com o CPF: " + cpf);
        }

        // 3. BLOQUEIO DE SEGURANÇA: Verifica se o e-mail foi validado
        if (!usuario.isEnabled()) {
            throw new UsernameNotFoundException("Conta ainda não ativada. Por favor, verifique seu e-mail para validar o cadastro.");
        }

        // 4. Normaliza a Role (Garante que tenha o prefixo ROLE_)
        String roleNome = usuario.getRole();
        if (roleNome == null) roleNome = "ROLE_USER"; 
        if (!roleNome.startsWith("ROLE_")) {
            roleNome = "ROLE_" + roleNome;
        }

        // 5. Retorna o objeto User do Spring Security
        return new User(
                usuario.getCpf(),
                usuario.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(roleNome))
        );
    }
}