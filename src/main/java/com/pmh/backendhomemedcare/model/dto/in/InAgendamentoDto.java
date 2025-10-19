package com.pmh.backendhomemedcare.model.dto.in;

import java.time.LocalDate;
import java.time.LocalTime;

public record InAgendamentoDto(
        Long pacienteId,
        Long profissionalId,   // opcional
        Long tipoAtendimento,
        LocalDate diaDesejado, // data do agendamento
        LocalTime horaDesejada, // horário do agendamento
        Integer diasSimulacao   // usado apenas na simulação
) {}
