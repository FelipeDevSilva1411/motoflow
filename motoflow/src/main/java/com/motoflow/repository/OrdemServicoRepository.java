package com.motoflow.repository;

import com.motoflow.model.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
    Optional<OrdemServico> findByCodigoRastreioIgnoreCase(String codigoRastreio);
    Optional<OrdemServico> findFirstByMotoPlacaIgnoreCaseOrderByDataEntradaDesc(String placa);
    List<OrdemServico> findByMotoPlacaIgnoreCaseOrderByDataEntradaDesc(String placa);
    // Traz apenas as ordens que ainda NÃO foram entregues
    List<OrdemServico> findByStatusNot(com.motoflow.model.StatusOS status);
}