package com.pmh.backendhomemedcare.model.dto.out;

import com.pmh.backendhomemedcare.model.enums.TipoDisponibilidade;

import java.time.LocalDate;
import java.time.LocalTime;

public record DisponibilidadeDia(
        LocalDate data,
        LocalTime inicio,
        LocalTime fim,
        TipoDisponibilidade tipo
) {}
