package com.pmh.backendhomemedcare.model.entity;

import com.pmh.backendhomemedcare.model.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String nome;

    @Column(nullable=false, unique=true)
    private String email;

    @Column(nullable=false, length=120)
    private String senhaHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    private boolean ativo = true;

    private LocalDateTime criadoEm = LocalDateTime.now();
}
