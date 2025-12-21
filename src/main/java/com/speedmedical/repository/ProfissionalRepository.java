package com.speedmedical.repository;

import com.speedmedical.entity.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {
    Profissional findByUsuarioId(Long usuarioId);
    Profissional findByCrm(String crm);
}