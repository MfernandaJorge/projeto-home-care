package com.pmh.backendhomemedcare.model.dto.out;

import java.time.LocalDate;
import java.time.LocalTime;

public record OutSugestaoAgendamentoDto(
        String profissional,
        LocalDate data,
        LocalTime horaInicio,
        double tempoTotalMin
) {}
