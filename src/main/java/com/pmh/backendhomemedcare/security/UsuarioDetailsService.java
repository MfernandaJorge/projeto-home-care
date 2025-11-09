package com.pmh.backendhomemedcare.security;

import com.pmh.backendhomemedcare.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioRepository repo;
    public UsuarioDetailsService(UsuarioRepository repo) { this.repo = repo; }

    @Override
    public UsuarioPrincipal loadUserByUsername(String email) {
        var usuario = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return new UsuarioPrincipal(usuario);
    }
}
