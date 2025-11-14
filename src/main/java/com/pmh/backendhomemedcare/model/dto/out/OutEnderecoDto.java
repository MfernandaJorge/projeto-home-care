package com.pmh.backendhomemedcare.model.dto.out;

public record OutEnderecoDto(
        String logradouro,
        String bairro,
        String cidade,
        String estado,
        String cep,
        int numero,
        double distanceKm,
        double durationMin,
        Double latitude,
        Double longitude
) {}

