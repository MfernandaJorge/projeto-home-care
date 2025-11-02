package com.pmh.backendhomemedcare.model.dto.out;

import java.time.Instant;
import java.util.Set;

public record OutAuthDto(
        String token,
        Instant expiraEm,
        String nome,
        String email,
        Set<String> roles
) {}
