package com.vidaplus.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vidaplus.entity.Usuario;
import com.vidaplus.repository.UsuarioRepository;
import com.vidaplus.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public Usuario buscarPorLogin(String login) {
        return usuarioRepository.findByUsernameOrCpf(login);
    }
}