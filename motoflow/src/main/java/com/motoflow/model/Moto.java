package com.motoflow.model;

import jakarta.persistence.*;

@Entity
@Table(name = "motos")
public class Moto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String placa;

    @Column(nullable = false)
    private String modelo;

    private Integer cilindrada;
    
    private Integer kmAtual;

    // Relacionamento: Várias motos podem pertencer a um Cliente
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente clienteProprietario;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    
    public Integer getCilindrada() { return cilindrada; }
    public void setCilindrada(Integer cilindrada) { this.cilindrada = cilindrada; }
    
    public Integer getKmAtual() { return kmAtual; }
    public void setKmAtual(Integer kmAtual) { this.kmAtual = kmAtual; }
    
    public Cliente getClienteProprietario() { return clienteProprietario; }
    public void setClienteProprietario(Cliente clienteProprietario) { this.clienteProprietario = clienteProprietario; }
}