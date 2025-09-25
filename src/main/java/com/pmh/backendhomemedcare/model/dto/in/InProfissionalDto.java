package com.pmh.backendhomemedcare.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InProfissionalDto(String nome,
                                String documento,
                                @JsonProperty("endereco") InEnderecoDto inEnderecoDto,
                                int ocupacao,
                                int telefone,
                                String email) {
}
