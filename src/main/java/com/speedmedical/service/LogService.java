package com.speedmedical.service;

import com.speedmedical.entity.Log;
import com.speedmedical.entity.Usuario;
import com.speedmedical.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    public void salvarLog(Usuario usuario, String acao, String detalhes) {
        try {
            Log log = new Log(usuario, acao, detalhes);
            logRepository.save(log);
        } catch (Exception e) {
            System.err.println("Erro ao salvar log: " + e.getMessage());
            // Não lançamos erro para não travar o sistema se o log falhar
        }
    }
}