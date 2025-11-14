package com.pmh.backendhomemedcare.model.dto.in;

import java.util.List;

public record InJornadaDto(List<InJornadaDiaDto> diasSemana, List<InJornadaExcecaoDto> excecoes) {}

