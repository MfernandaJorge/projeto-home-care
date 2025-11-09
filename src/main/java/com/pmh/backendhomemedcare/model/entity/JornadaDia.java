package com.pmh.backendhomemedcare.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Data
public class JornadaDia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DayOfWeek diaSemana;

    private LocalTime inicio;
    private LocalTime fim;

    private LocalTime inicioAlmoco;
    private LocalTime fimAlmoco;

    private boolean trabalha; // se o profissional trabalha neste dia

    @ManyToOne
    @JoinColumn(name = "jornada_id")
    private JornadaProfissional jornada;
}
