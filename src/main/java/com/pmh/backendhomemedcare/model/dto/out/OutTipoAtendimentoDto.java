package com.pmh.backendhomemedcare.model.dto.out;

public record OutTipoAtendimentoDto(
        Long id,
        String descricao,
        int duracaoMinutos,
        boolean ativo
) { }
