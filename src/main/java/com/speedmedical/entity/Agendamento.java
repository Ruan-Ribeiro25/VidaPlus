package com.speedmedical.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String procedimento; // Ex: "Cardiologista", "Raio-X"
    private String profissional; // Ex: "Dr. House"
    
    @Column(name = "data_hora")
    private LocalDateTime dataHora; // Data e hora da consulta
    
    private String status; // "Agendado", "Concluído", "Cancelado"

    // Relacionamento: Muitos agendamentos para Um usuário
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Agendamento() {}

    // Construtor útil para criar rápido
    public Agendamento(String procedimento, String profissional, LocalDateTime dataHora, String status, Usuario usuario) {
        this.procedimento = procedimento;
        this.profissional = profissional;
        this.dataHora = dataHora;
        this.status = status;
        this.usuario = usuario;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProcedimento() { return procedimento; }
    public void setProcedimento(String procedimento) { this.procedimento = procedimento; }

    public String getProfissional() { return profissional; }
    public void setProfissional(String profissional) { this.profissional = profissional; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}