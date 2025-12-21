package com.speedmedical.repository;

import com.speedmedical.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByUsername(String username);
    Usuario findByEmail(String email);
    Usuario findByCpf(String cpf);

    // --- CORREÇÃO DO ERRO DA LINHA 65 ---
    // O Service precisa deste método para ativar a conta
    Usuario findByCodigoVerificacao(String codigoVerificacao);
    // ------------------------------------

    // --- CORREÇÃO DO ERRO DE LOGIN (Ruan ou CPF) ---
    // Permite logar tanto pelo nome quanto pelo CPF
    Usuario findByUsernameOrCpf(String username, String cpf);
    // -----------------------------------------------

    List<Usuario> findByRole(String role);
    
    @Query("SELECT u FROM Usuario u JOIN u.polos p WHERE p.id = :poloId AND u.role = :role")
    List<Usuario> findByPoloIdAndRole(@Param("poloId") Long poloId, @Param("role") String role);
}