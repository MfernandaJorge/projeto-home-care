package com.pmh.backendhomemedcare.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
public class JornadaExcecao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate data;

    private boolean folga; // true = folga, false = plantão extra
    private LocalTime inicio;
    private LocalTime fim;

    @ManyToOne
    @JoinColumn(name = "jornada_id")
    private JornadaProfissional jornada;
}
