package com.pmh.backendhomemedcare.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class JornadaProfissional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** horário padrão por dia da semana **/
    @OneToMany(mappedBy = "jornada", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JornadaDia> diasSemana;

    /** folgas e plantões específicos **/
    @OneToMany(mappedBy = "jornada", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JornadaExcecao> excecoes;
}
