package com.speedmedical.service;

import com.speedmedical.entity.Documento;
import java.util.List;

public interface DocumentoService {
    void save(Documento documento);
    List<Documento> findAll();
}
