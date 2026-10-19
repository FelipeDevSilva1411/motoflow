package com.motoflow.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clienteNome;

    @Column(nullable = false)
    private String telefoneWhatsapp;

    private String motoModelo;
    private String motoPlaca;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(length = 1000)
    private String servicoPrevisto;

    @ManyToOne
    @JoinColumn(name = "mecanico_id")
    private Mecanico mecanicoPreferencial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status = StatusAgendamento.PENDENTE;

    // Construtores
    public Agendamento() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }

    public String getTelefoneWhatsapp() { return telefoneWhatsapp; }
    public void setTelefoneWhatsapp(String telefoneWhatsapp) { this.telefoneWhatsapp = telefoneWhatsapp; }

    public String getMotoModelo() { return motoModelo; }
    public void setMotoModelo(String motoModelo) { this.motoModelo = motoModelo; }

    public String getMotoPlaca() { return motoPlaca; }
    public void setMotoPlaca(String motoPlaca) { this.motoPlaca = motoPlaca; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getServicoPrevisto() { return servicoPrevisto; }
    public void setServicoPrevisto(String servicoPrevisto) { this.servicoPrevisto = servicoPrevisto; }

    public Mecanico getMecanicoPreferencial() { return mecanicoPreferencial; }
    public void setMecanicoPreferencial(Mecanico mecanicoPreferencial) { this.mecanicoPreferencial = mecanicoPreferencial; }

    public StatusAgendamento getStatus() { return status; }
    public void setStatus(StatusAgendamento status) { this.status = status; }
}