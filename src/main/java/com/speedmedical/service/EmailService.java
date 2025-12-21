package com.speedmedical.service;

public interface EmailService {
    void enviarEmailConfirmacao(String destinatario, String nome, String codigo);
}