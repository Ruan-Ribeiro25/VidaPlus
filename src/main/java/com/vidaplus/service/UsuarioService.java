package com.vidaplus.service;

import com.vidaplus.entity.Usuario;

public interface UsuarioService {

    Usuario findById(Long id);

    Usuario buscarPorLogin(String login);
}