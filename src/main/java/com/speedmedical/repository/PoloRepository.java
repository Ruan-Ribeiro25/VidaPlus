package com.speedmedical.repository;

import com.speedmedical.entity.Polo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoloRepository extends JpaRepository<Polo, Long> {
    Polo findByCep(String cep);
    Polo findByCidade(String cidade);
}