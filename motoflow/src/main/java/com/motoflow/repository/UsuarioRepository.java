package com.motoflow.repository;

import com.motoflow.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Método que o sistema de login vai usar para procurar o utilizador
    Optional<Usuario> findByLogin(String login);
}