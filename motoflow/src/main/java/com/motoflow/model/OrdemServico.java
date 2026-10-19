package com.motoflow.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordens_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigoRastreio;

    // 1. Relacionamento Normalizado com o Cliente
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // 2. Relacionamento Normalizado com a Moto
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "moto_id", nullable = false)
    private Moto moto;

    // 3. Relacionamento com a Equipa/Mecânico
    @ManyToOne
    @JoinColumn(name = "mecanico_id")
    private Mecanico mecanicoResponsavel;

    private LocalDateTime dataEntrada = LocalDateTime.now();
    private LocalDateTime dataEntrega;

    @Enumerated(EnumType.STRING)
    private StatusOS status = StatusOS.RECEBIDA;

    private Boolean capaceteDeixado = false;
    private Boolean bauChaveDeixada = false;
    private String nivelCombustivel;
    private String estadoRelacao;
    
    @Column(length = 1000)
    private String relatoCliente;
    
    @Column(length = 1000)
    private String avariasPrevia;

    // Relacionamento com os Itens (Peças e Serviços)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ordem_servico_id")
    private List<ItemOS> itens = new ArrayList<>();

    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento;

    private BigDecimal desconto = BigDecimal.ZERO;
    private BigDecimal valorFinal = BigDecimal.ZERO;
    private BigDecimal valorPago = BigDecimal.ZERO;
    private BigDecimal troco = BigDecimal.ZERO;
    
    private Boolean pago = false;
    private LocalDateTime dataPagamento;

    // Método Utilitário
    public void recalcularTotal() {
        if (this.itens != null) {
            this.valorTotal = this.itens.stream()
                .map(item -> item.getValorUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            this.valorTotal = BigDecimal.ZERO;
        }
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCodigoRastreio() { return codigoRastreio; }
    public void setCodigoRastreio(String codigoRastreio) { this.codigoRastreio = codigoRastreio; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    public Moto getMoto() { return moto; }
    public void setMoto(Moto moto) { this.moto = moto; }
    
    public Mecanico getMecanicoResponsavel() { return mecanicoResponsavel; }
    public void setMecanicoResponsavel(Mecanico mecanicoResponsavel) { this.mecanicoResponsavel = mecanicoResponsavel; }
    
    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDateTime dataEntrada) { this.dataEntrada = dataEntrada; }
    
    public LocalDateTime getDataEntrega() { return dataEntrega; }
    public void setDataEntrega(LocalDateTime dataEntrega) { this.dataEntrega = dataEntrega; }
    
    public StatusOS getStatus() { return status; }
    public void setStatus(StatusOS status) { this.status = status; }
    
    public Boolean getCapaceteDeixado() { return capaceteDeixado; }
    public void setCapaceteDeixado(Boolean capaceteDeixado) { this.capaceteDeixado = capaceteDeixado; }
    
    public Boolean getBauChaveDeixada() { return bauChaveDeixada; }
    public void setBauChaveDeixada(Boolean bauChaveDeixada) { this.bauChaveDeixada = bauChaveDeixada; }
    
    public String getNivelCombustivel() { return nivelCombustivel; }
    public void setNivelCombustivel(String nivelCombustivel) { this.nivelCombustivel = nivelCombustivel; }
    
    public String getEstadoRelacao() { return estadoRelacao; }
    public void setEstadoRelacao(String estadoRelacao) { this.estadoRelacao = estadoRelacao; }
    
    public String getRelatoCliente() { return relatoCliente; }
    public void setRelatoCliente(String relatoCliente) { this.relatoCliente = relatoCliente; }
    
    public String getAvariasPrevia() { return avariasPrevia; }
    public void setAvariasPrevia(String avariasPrevia) { this.avariasPrevia = avariasPrevia; }
    
    public List<ItemOS> getItens() { return itens; }
    public void setItens(List<ItemOS> itens) { this.itens = itens; }
    
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    
    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(FormaPagamento formaPagamento) { this.formaPagamento = formaPagamento; }
    
    public BigDecimal getDesconto() { return desconto; }
    public void setDesconto(BigDecimal desconto) { this.desconto = desconto; }
    
    public BigDecimal getValorFinal() { return valorFinal; }
    public void setValorFinal(BigDecimal valorFinal) { this.valorFinal = valorFinal; }
    
    public BigDecimal getValorPago() { return valorPago; }
    public void setValorPago(BigDecimal valorPago) { this.valorPago = valorPago; }
    
    public BigDecimal getTroco() { return troco; }
    public void setTroco(BigDecimal troco) { this.troco = troco; }
    
    public Boolean getPago() { return pago != null && pago; }
    public void setPago(Boolean pago) { this.pago = pago; }
    
    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDateTime dataPagamento) { this.dataPagamento = dataPagamento; }
}