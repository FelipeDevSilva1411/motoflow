package com.motoflow.controller;

import com.motoflow.model.Usuario;
import com.motoflow.repository.UsuarioRepository;
import com.motoflow.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public record LoginDTO(String login, String senha) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dados) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(dados.login().trim().toLowerCase());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("erro", "Utilizador ou senha incorretos."));
        }

        Usuario usuario = usuarioOpt.get();

        // Valida o hash da senha
        if (!passwordEncoder.matches(dados.senha(), usuario.getSenha())) {
            return ResponseEntity.status(401).body(Map.of("erro", "Utilizador ou senha incorretos."));
        }

        // Gera o token JWT
        String token = jwtService.gerarToken(usuario.getLogin(), usuario.getCargo(), usuario.getNome());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "nome", usuario.getNome(),
                "cargo", usuario.getCargo()
        ));
    }
}