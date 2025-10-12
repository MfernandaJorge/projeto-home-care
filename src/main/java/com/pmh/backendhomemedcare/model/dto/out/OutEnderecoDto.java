package com.pmh.backendhomemedcare.model.dto.out;

public record OutEnderecoDto(
        String logradouro,
        String bairro,
        String cidade,
        String estado,
        String cep,
        Integer numero,
        Double distanceKm,
        Double durationMin
) {}
