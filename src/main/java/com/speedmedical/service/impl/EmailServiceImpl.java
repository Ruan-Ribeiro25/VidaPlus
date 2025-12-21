package com.speedmedical.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.speedmedical.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void enviarEmailConfirmacao(String destinatario, String nome, String codigo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Ativação de Conta - SpeedMedical");
        
        String texto = "Olá, " + nome + "!\n\n" +
                "Seu cadastro no SpeedMedical foi realizado com sucesso.\n" +
                "Para ativar sua conta, acesse o link abaixo ou digite o código na tela de verificação:\n\n" +
                "CÓDIGO DE ATIVAÇÃO: " + codigo + "\n\n" +
                "Link direto: http://localhost:8080/verificar-conta?codigo=" + codigo + "\n\n" +
                "Atenciosamente,\nEquipe SpeedMedical";
                
        message.setText(texto);
        javaMailSender.send(message);
    }
}