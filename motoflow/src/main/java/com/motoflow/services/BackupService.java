package com.motoflow.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.motoflow.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class BackupService {

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MotoRepository motoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private LocalDateTime ultimoBackup = LocalDateTime.now();

    public LocalDateTime getUltimoBackup() {
        return ultimoBackup;
    }

    // Coleta todas as entidades em um mapa consolidado
    public Map<String, Object> coletarDadosCompletos() {
        Map<String, Object> dados = new HashMap<>();
        dados.put("versaoSistema", "MotoFlow PRO 1.0");
        dados.put("geradoEm", LocalDateTime.now().toString());
        dados.put("clientes", clienteRepository.findAll());
        dados.put("motos", motoRepository.findAll());
        dados.put("usuarios", usuarioRepository.findAll());
        dados.put("ordensServico", ordemServicoRepository.findAll());
        return dados;
    }

    // Converte os dados em JSON formatado
    public byte[] gerarArquivoJsonBackup() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        Map<String, Object> dados = coletarDadosCompletos();
        String json = mapper.writeValueAsString(dados);
        
        this.ultimoBackup = LocalDateTime.now();
        return json.getBytes(StandardCharsets.UTF_8);
    }

    // Rotina automática: roda todos os dias às 02:00 da madrugada
    @Scheduled(cron = "0 0 2 * * ?")
    public void executarBackupAutomatico() {
        try {
            File pastaBackup = new File("./backups");
            if (!pastaBackup.exists()) {
                pastaBackup.mkdirs();
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File arquivo = new File(pastaBackup, "backup_motoflow_" + timestamp + ".json");

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            try (FileWriter writer = new FileWriter(arquivo, StandardCharsets.UTF_8)) {
                mapper.writeValue(writer, coletarDadosCompletos());
            }

            this.ultimoBackup = LocalDateTime.now();
            System.out.println(">>> [BACKUP AUTOMÁTICO] Arquivo salvo em: " + arquivo.getAbsolutePath());
        } catch (Exception e) {
            System.err.println(">>> [ERRO BACKUP AUTOMÁTICO] " + e.getMessage());
        }
    }
}