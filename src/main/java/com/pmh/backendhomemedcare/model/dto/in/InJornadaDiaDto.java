package com.pmh.backendhomemedcare.model.dto.in;

import java.time.LocalTime;
import java.time.DayOfWeek;

public record InJornadaDiaDto(DayOfWeek diaSemana,
                               LocalTime inicio,
                               LocalTime fim,
                               LocalTime inicioAlmoco,
                               LocalTime fimAlmoco,
                               boolean trabalha) {}

