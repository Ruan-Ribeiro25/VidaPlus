package com.vidaplus.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table; // Import Adicionado
import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos") // <--- Adicionado para travar na tabela certa
public class Agendamento extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    private String procedimento;
    private String profissional;
    private LocalDateTime dataHora;
    private String status;

    @ManyToOne
    private Usuario usuario;

    public Agendamento() {}

    public Agendamento(String procedimento, String profissional, LocalDateTime dataHora, String status, Usuario usuario) {
        this.procedimento = procedimento;
        this.profissional = profissional;
        this.dataHora = dataHora;
        this.status = status;
        this.usuario = usuario;
    }

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