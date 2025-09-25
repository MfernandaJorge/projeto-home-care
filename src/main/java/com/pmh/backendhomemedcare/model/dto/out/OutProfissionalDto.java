package com.pmh.backendhomemedcare.model.dto.out;

public record OutProfissionalDto(
        Long id,
        String nome,
        String documento,
        OutEnderecoDto endereco,
        Integer ocupacao,
        Integer telefone,
        String email
) {}
