package com.motoflow.controller;

import com.motoflow.model.Mecanico;
import com.motoflow.repository.MecanicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mecanicos")
@CrossOrigin(origins = "*")
public class MecanicoController {

    @Autowired
    private MecanicoRepository mecanicoRepository;

    @GetMapping
    public List<Mecanico> listar() {
        return mecanicoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Mecanico> criar(@RequestBody Mecanico mecanico) {
        return ResponseEntity.ok(mecanicoRepository.save(mecanico));
    }
}