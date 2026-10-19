package com.motoflow.controller;

import com.motoflow.model.Agendamento;
import com.motoflow.model.StatusAgendamento;
import com.motoflow.repository.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "*")
public class AgendamentoController {

    @Autowired
    private AgendamentoRepository repository;

    // 1. Listar todos em ordem cronológica
    @GetMapping
    public List<Agendamento> listarTodos() {
        return repository.findAllByOrderByDataHoraAsc();
    }

    // 2. Criar novo agendamento
    @PostMapping
    public ResponseEntity<Agendamento> criar(@RequestBody Agendamento agendamento) {
        if (agendamento.getStatus() == null) {
            agendamento.setStatus(StatusAgendamento.PENDENTE);
        }
        return ResponseEntity.ok(repository.save(agendamento));
    }

    // 3. Atualizar Status (Confirmar, Cancelar ou Converteu em OS)
    @Transactional
    @PatchMapping("/{id}/status")
    public ResponseEntity<Agendamento> atualizarStatus(
            @PathVariable Long id, 
            @RequestParam StatusAgendamento status) {
        return repository.findById(id).map(ag -> {
            ag.setStatus(status);
            return ResponseEntity.ok(repository.save(ag));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 4. Remover agendamento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}