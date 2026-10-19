package com.motoflow.controller;

import com.motoflow.services.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/backup")
@CrossOrigin(origins = "*")
public class BackupController {

    @Autowired
    private BackupService backupService;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> obterStatus() {
        String dataBackup = (backupService != null && backupService.getUltimoBackup() != null)
                ? backupService.getUltimoBackup().toString()
                : LocalDateTime.now().toString();

        boolean precisaBackup = backupService != null && backupService.getUltimoBackup() != null
                && backupService.getUltimoBackup().isBefore(LocalDateTime.now().minusDays(1));

        return ResponseEntity.ok(Map.of(
                "ultimoBackup", dataBackup,
                "precisaBackup", precisaBackup
        ));
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> baixarBackup() {
        try {
            byte[] bytes = backupService.gerarArquivoJsonBackup();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String nomeArquivo = "motoflow_backup_" + timestamp + ".json";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(bytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}