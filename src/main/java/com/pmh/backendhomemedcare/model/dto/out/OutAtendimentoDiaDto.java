package com.pmh.backendhomemedcare.model.dto.out;

import java.time.LocalDateTime;

public record OutAtendimentoDiaDto(
        Long id,
        String paciente,
        String profissional,
        LocalDateTime inicio,
        LocalDateTime fim,
        boolean cancelado,
        boolean concluido
) {}
