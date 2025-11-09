package com.pmh.backendhomemedcare.model.dto.in;

import java.util.Set;

public record InCriarUsuarioDto(
        String nome,
        String email,
        String senha,
        Set<String> roles
) {}
