package com.pmh.backendhomemedcare.config;

import com.pmh.backendhomemedcare.model.entity.Usuario;
import com.pmh.backendhomemedcare.model.enums.Role;
import com.pmh.backendhomemedcare.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Set;

// Classe para popular o banco de dados com um usuário ADMIN para teste. Em produção devemos vamos trabalhar com variáveis de ambiente
@Component
public class AdminSeed {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public AdminSeed(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @PostConstruct
    public void init() {
        String email = "admin@homemedcare.com";
        if (!repo.existsByEmail(email)) {
            Usuario u = new Usuario();
            u.setNome("Administrador");
            u.setEmail(email);
            u.setSenhaHash(encoder.encode("admin"));
            u.setRoles(Set.of(Role.ROLE_ADMIN));
            u.setAtivo(true);
            repo.save(u);
            System.out.println("✅ ADMIN criado: " + email + " / senha: admin");
        }
    }
}
