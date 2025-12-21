package com.speedmedical.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "profissional")
public class Profissional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento com a tabela principal de Usuários
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private String crm;
    private String matricula;
    private String atuacao; // Especialidade
    
    // --- NOVO CAMPO NECESSÁRIO PARA O ERRO SUMIR ---
    @Column(name = "status_aprovacao")
    private String statusAprovacao; // Valores: PENDENTE, APROVADO

    public Profissional() {}

    // --- GETTERS E SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public String getCrm() { return crm; }
    public void setCrm(String crm) { this.crm = crm; }
    
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    
    public String getAtuacao() { return atuacao; }
    public void setAtuacao(String atuacao) { this.atuacao = atuacao; }

    // O Controller precisa destes métodos abaixo:
    public String getStatusAprovacao() { return statusAprovacao; }
    public void setStatusAprovacao(String statusAprovacao) { this.statusAprovacao = statusAprovacao; }
}