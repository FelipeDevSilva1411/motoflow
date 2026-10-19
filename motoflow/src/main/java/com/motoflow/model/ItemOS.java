package com.motoflow.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "itens_os")
public class ItemOS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private Integer quantidade = 1;

    @Column(nullable = false)
    private BigDecimal valorUnitario = BigDecimal.ZERO; // Preço cobrado ao cliente

    private BigDecimal valorCusto = BigDecimal.ZERO; // Preço de custo pago pela oficina

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoItem tipo = TipoItem.PECA; // PECA ou SERVICO

    // Construtores
    public ItemOS() {}

    public ItemOS(String descricao, Integer quantidade, BigDecimal valorUnitario, BigDecimal valorCusto, TipoItem tipo) {
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.valorCusto = valorCusto != null ? valorCusto : BigDecimal.ZERO;
        this.tipo = tipo;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }

    public BigDecimal getValorCusto() { return valorCusto != null ? valorCusto : BigDecimal.ZERO; }
    public void setValorCusto(BigDecimal valorCusto) { this.valorCusto = valorCusto; }

    public TipoItem getTipo() { return tipo; }
    public void setTipo(TipoItem tipo) { this.tipo = tipo; }
}