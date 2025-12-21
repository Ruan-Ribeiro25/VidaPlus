package com.speedmedical.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documentos")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeArquivo;
    private String tipoArquivo;
    
    @Column(name = "data_upload")
    private LocalDateTime dataUpload;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] dados;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // --- O CAMPO QUE FALTAVA ---
    @ManyToOne
    @JoinColumn(name = "agendamento_id")
    private Agendamento agendamento;

    public Documento() {}

    // --- CONSTRUTOR QUE O CONTROLLER ESTÁ PROCURANDO ---
    public Documento(String nomeArquivo, String tipoArquivo, byte[] dados, Usuario usuario, Agendamento agendamento) {
        this.nomeArquivo = nomeArquivo;
        this.tipoArquivo = tipoArquivo;
        this.dados = dados;
        this.usuario = usuario;
        this.agendamento = agendamento;
        this.dataUpload = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }

    public String getTipoArquivo() { return tipoArquivo; }
    public void setTipoArquivo(String tipoArquivo) { this.tipoArquivo = tipoArquivo; }

    public LocalDateTime getDataUpload() { return dataUpload; }
    public void setDataUpload(LocalDateTime dataUpload) { this.dataUpload = dataUpload; }

    public byte[] getDados() { return dados; }
    public void setDados(byte[] dados) { this.dados = dados; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public Agendamento getAgendamento() { return agendamento; }
    public void setAgendamento(Agendamento agendamento) { this.agendamento = agendamento; }
}