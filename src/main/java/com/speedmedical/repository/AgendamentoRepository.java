package com.speedmedical.repository;

import com.speedmedical.entity.Agendamento;
import com.speedmedical.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    
    // Busca histórico do paciente
    List<Agendamento> findByUsuario(Usuario usuario);

    // --- NOVO: Verifica se já existe agendamento neste horário para este profissional ---
    // Retorna 'true' se encontrar, ignorando os 'Cancelados'
    boolean existsByProfissionalAndDataHoraAndStatusNot(String profissional, LocalDateTime dataHora, String status);
}