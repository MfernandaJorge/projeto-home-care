package com.pmh.backendhomemedcare.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Paciente paciente;

    @ManyToOne
    private Profissional profissional;

    @ManyToOne(optional = false)
    private TipoAtendimento tipoAtendimento;

    private LocalDateTime inicio;
    private LocalDateTime fim;

    private boolean concluido = false;
    private boolean cancelado = false;
}

