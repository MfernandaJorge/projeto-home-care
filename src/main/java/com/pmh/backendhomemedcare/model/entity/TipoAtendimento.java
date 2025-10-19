package com.pmh.backendhomemedcare.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TipoAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String descricao;

    @Column(nullable = false)
    private int duracaoMinutos;

    @Column(nullable = false)
    private boolean ativo = true;
}
