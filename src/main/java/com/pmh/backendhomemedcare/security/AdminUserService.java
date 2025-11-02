package com.pmh.backendhomemedcare.security;

import com.pmh.backendhomemedcare.model.dto.in.InCriarUsuarioDto;
import com.pmh.backendhomemedcare.model.entity.Usuario;
import com.pmh.backendhomemedcare.model.enums.Role;
import com.pmh.backendhomemedcare.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder encoder;

    public AdminUserService(UsuarioRepository usuarioRepo, PasswordEncoder encoder) {
        this.usuarioRepo = usuarioRepo;
        this.encoder = encoder;
    }

    @Transactional
    public Long criarUsuario(InCriarUsuarioDto dto) {
        if (dto == null || dto.email() == null || dto.email().isBlank()
                || dto.senha() == null || dto.senha().isBlank()
                || dto.nome() == null || dto.nome().isBlank()) {
            throw new IllegalArgumentException("Nome, e-mail e senha são obrigatórios.");
        }
        String email = dto.email().toLowerCase().trim();
        if (usuarioRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        Set<Role> roles = (dto.roles() == null || dto.roles().isEmpty())
                ? Set.of(Role.ROLE_OPERADOR)
                : dto.roles().stream()
                .map(String::trim)
                .map(String::toUpperCase)
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .map(Role::valueOf)
                .collect(Collectors.toSet());

        Usuario u = new Usuario();
        u.setNome(dto.nome().trim());
        u.setEmail(email);
        u.setSenhaHash(encoder.encode(dto.senha()));
        u.setRoles(roles);
        u.setAtivo(true);

        usuarioRepo.save(u);
        return u.getId();
    }
}
