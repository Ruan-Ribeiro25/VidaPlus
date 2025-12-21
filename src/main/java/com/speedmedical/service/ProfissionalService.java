package com.speedmedical.service;

import com.speedmedical.entity.Profissional;
import java.util.List;

public interface ProfissionalService {
    void save(Profissional profissional);
    List<Profissional> findAll();
}
