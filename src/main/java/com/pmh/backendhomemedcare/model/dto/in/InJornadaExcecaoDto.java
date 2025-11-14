package com.pmh.backendhomemedcare.model.dto.in;

import java.time.LocalDate;
import java.time.LocalTime;

public record InJornadaExcecaoDto(LocalDate data,
                                   boolean folga,
                                   LocalTime inicio,
                                   LocalTime fim) {}

