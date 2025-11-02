package com.pmh.backendhomemedcare.security;

import com.pmh.backendhomemedcare.model.dto.in.InLoginDto;
import com.pmh.backendhomemedcare.model.dto.out.OutAuthDto;
import com.pmh.backendhomemedcare.model.entity.Usuario;
import com.pmh.backendhomemedcare.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder encoder; // mantido caso use em outro fluxo
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepo, PasswordEncoder encoder,
                       AuthenticationManager authManager, JwtService jwtService) {
        this.usuarioRepo = usuarioRepo;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    public OutAuthDto login(InLoginDto dto) {
        var token = new UsernamePasswordAuthenticationToken(dto.email(), dto.senha());
        authManager.authenticate(token); // valida credenciais

        Usuario user = usuarioRepo.findByEmail(dto.email().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String jwt = jwtService.generateToken(
                user.getEmail(),
                Map.of("name", user.getNome(), "roles", user.getRoles())
        );
        Instant exp = jwtService.getExpiration(jwt);

        return new OutAuthDto(
                jwt, exp, user.getNome(), user.getEmail(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet())
        );
    }
}
