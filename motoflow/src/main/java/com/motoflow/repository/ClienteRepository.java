package com.motoflow.repository;

import com.motoflow.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByTelefoneWhatsapp(String telefoneWhatsapp);
}