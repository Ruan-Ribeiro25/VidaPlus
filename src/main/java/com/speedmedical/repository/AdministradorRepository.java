package com.speedmedical.repository;

import com.speedmedical.entity.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    // Busca o perfil de admin através do ID do usuário (login)
    Administrador findByUsuarioId(Long usuarioId);
}