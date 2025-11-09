package com.pmh.backendhomemedcare.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record InPacienteDto(String nome,
                            String documento,
                            @JsonProperty("endereco") InEnderecoDto inEnderecoDto,
                            LocalDate dataNascimento,
                            String telefone,
                            String email) {
}