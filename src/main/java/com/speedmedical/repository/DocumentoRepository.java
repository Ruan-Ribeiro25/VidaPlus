package com.speedmedical.repository;

import com.speedmedical.entity.Documento;
import com.speedmedical.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    // Busca todos os documentos de um paciente
    List<Documento> findByUsuario(Usuario usuario);
}