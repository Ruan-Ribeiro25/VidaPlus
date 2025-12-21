package com.speedmedical.service.impl;

import com.speedmedical.entity.Profissional;
import com.speedmedical.repository.ProfissionalRepository; // <--- Mudamos de DAO para Repository
import com.speedmedical.service.ProfissionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProfissionalServiceImpl implements ProfissionalService {

    @Autowired
    private ProfissionalRepository profissionalRepository; // <--- Injeção do Repository

    @Override
    public void save(Profissional profissional) {
        profissionalRepository.save(profissional);
    }

    @Override
    public List<Profissional> findAll() {
        return profissionalRepository.findAll();
    }
    
    // Método extra útil: buscar perfil pelo ID do Usuário (login)
    public Profissional buscarPorUsuario(Long usuarioId) {
        return profissionalRepository.findByUsuarioId(usuarioId);
    }
}