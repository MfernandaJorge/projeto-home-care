package com.pmh.backendhomemedcare.model.dto.out;

import java.time.LocalDate;

public record OutPacienteDto(
        Long id,
        String nome,
        String documento,
        OutEnderecoDto endereco,
        LocalDate dataNascimento,
        int telefone,
        String email
) {}