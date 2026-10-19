package com.motoflow.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Páginas e recursos estáticos (HTML, CSS, JS) liberados para o navegador carregar
                        .requestMatchers(
                                "/",
                                "/*.html",
                                "/static/**",
                                "/error",
                                "/favicon.ico",
                                "/*.css",
                                "/*.js"
                        ).permitAll()

                        // 2. Endpoints da API que são públicos
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/os/rastreio/**").permitAll() // Consulta pública do cliente

                        // 3. TODAS as outras rotas da API exigem autenticação obrigatória via JWT
                        .requestMatchers("/api/**").authenticated()

                        // Qualquer outro pedido
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}