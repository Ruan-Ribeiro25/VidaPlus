package com.speedmedical.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_logs") // Nome exato da tabela no banco
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento com Usuario (Quem fez a ação)
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "username_registrado")
    private String usernameRegistrado; // Grava o nome (caso o usuário seja deletado depois)

    private String acao;     // Ex: LOGIN, CADASTRO
    
    @Column(columnDefinition = "TEXT")
    private String detalhes; // Ex: "Usuário logou com sucesso"

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    // Preenche a data/hora automaticamente antes de salvar
    @PrePersist
    protected void onCreate() {
        dataHora = LocalDateTime.now();
    }

    // --- CONSTRUTORES (Importante para o Service funcionar) ---

    // 1. Construtor Vazio (Obrigatório para o Hibernate)
    public Log() {
    }

    // 2. Construtor com argumentos (Para usarmos no LogService)
    public Log(Usuario usuario, String acao, String detalhes) {
        this.usuario = usuario;
        if (usuario != null) {
            this.usernameRegistrado = usuario.getUsername();
        }
        this.acao = acao;
        this.detalhes = detalhes;
    }

    // --- GETTERS E SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getUsernameRegistrado() {
        return usernameRegistrado;
    }

    public void setUsernameRegistrado(String usernameRegistrado) {
        this.usernameRegistrado = usernameRegistrado;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}