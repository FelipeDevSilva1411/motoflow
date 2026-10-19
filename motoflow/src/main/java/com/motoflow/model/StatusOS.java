package com.motoflow.model;

public enum StatusOS {
    RECEBIDA("Moto Recebida no Pátio"),
    ORCAMENTO("Em Diagnóstico / Orçamento"),
    APROVADA("Aprovada pelo Cliente"),
    EM_MANUTENCAO("Mecânico Trabalhando"),
    TESTE_RODAGEM("Teste de Pista / Rodagem"),
    PRONTA("Pronta para Retirada"),
    FINALIZADA("Entregue ao Cliente"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusOS(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}