package com.pmh.backendhomemedcare.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String logradouro;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private int numero;

    private double distanceKm;    // distância até a clínica (mantém)
    private double durationMin;   // tempo de deslocamento (mantém)

    // novas colunas para cache de geocoding
    private Double latitude;
    private Double longitude;
    private LocalDateTime geocodedAt; // data/hora da última obtenção
}
