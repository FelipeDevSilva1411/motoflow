package com.motoflow.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    // Chave secreta de 256 bits para assinatura do token
    private static final String SECRET_KEY = "MotoFlowSuperSecretKeyParaOficinasDeEliteComAltaSeguranca2026!";
    private static final long TEMPO_EXPIRACAO_MS = 86400000L; // 24 horas de validade

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // Gera o Token JWT para o utilizador
    public String gerarToken(String login, String cargo, String nome) {
        return Jwts.builder()
                .subject(login)
                .claim("cargo", cargo)
                .claim("nome", nome)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + TEMPO_EXPIRACAO_MS))
                .signWith(getSigningKey())
                .compact();
    }

    // Extrai o login de dentro do Token
    public String extrairLogin(String token) {
        return obterClaims(token).getSubject();
    }

    // Verifica se o Token ainda é válido
    public boolean tokenValido(String token) {
        try {
            return !obterClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims obterClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}