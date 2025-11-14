package com.pmh.backendhomemedcare.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InProfissionalDto(String nome,
                                String documento,
                                @JsonProperty("endereco") InEnderecoDto inEnderecoDto,
                                int ocupacao,
                                String telefone,
                                String email,
                                Long jornadaId,
                                @JsonProperty("jornada") InJornadaDto jornadaDto) {
}
