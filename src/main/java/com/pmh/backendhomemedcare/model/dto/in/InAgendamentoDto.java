package com.pmh.backendhomemedcare.model.dto.in;

import java.time.LocalDate;
import java.time.LocalTime;

public record InAgendamentoDto(
        Long pacienteId,
        Long profissionalId,
        Long tipoAtendimento,
        LocalDate diaDesejado,
        LocalTime horaDesejada,
        Integer diasSimulacao
) {}
