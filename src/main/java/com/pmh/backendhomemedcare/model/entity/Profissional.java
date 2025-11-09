package com.pmh.backendhomemedcare.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Profissional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String documento;

    @ManyToOne
    @JoinColumn(name = "endereco_id", nullable = false)
    private Endereco endereco;

    private int ocupacao;

    private String telefone;

    private String email;

}