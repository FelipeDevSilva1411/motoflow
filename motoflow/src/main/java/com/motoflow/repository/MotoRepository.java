package com.motoflow.repository;

import com.motoflow.model.Moto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MotoRepository extends JpaRepository<Moto, Long> {
    Optional<Moto> findByPlacaIgnoreCase(String placa);
}