package com.motoflow.security;

import com.motoflow.model.Mecanico;
import com.motoflow.model.Usuario;
import com.motoflow.repository.MecanicoRepository;
import com.motoflow.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository, 
                                      MecanicoRepository mecanicoRepository, 
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Inicializa o utilizador Admin
            if (usuarioRepository.findByLogin("admin").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNome("Felipe (Admin)");
                admin.setLogin("admin");
                admin.setSenha(passwordEncoder.encode("admin123"));
                admin.setCargo("ADMIN");
                usuarioRepository.save(admin);
            }

            // 2. Inicializa os Mecânicos padrão com 40% de comissão
            if (mecanicoRepository.count() == 0) {
                Mecanico m1 = new Mecanico();
                m1.setNome("Jean");
                m1.setPercentualComissao(new BigDecimal("40.00"));
                m1.setAtivo(true);
                mecanicoRepository.save(m1);

                Mecanico m2 = new Mecanico();
                m2.setNome("Victor");
                m2.setPercentualComissao(new BigDecimal("40.00"));
                m2.setAtivo(true);
                mecanicoRepository.save(m2);
                
                System.out.println(">>> [MOTOFLOW] Mecânicos padrão (Jean e Victor) criados com 40% de comissão.");
            }
        };
    }
}