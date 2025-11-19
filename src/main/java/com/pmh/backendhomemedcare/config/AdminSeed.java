package com.pmh.backendhomemedcare.config;

import com.pmh.backendhomemedcare.model.entity.Usuario;
import com.pmh.backendhomemedcare.model.enums.Role;
import com.pmh.backendhomemedcare.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

// Classe para popular o banco de dados com um usuário ADMIN para teste. Em produção devemos vamos trabalhar com variáveis de ambiente
@Component
public class AdminSeed {

    private static final Logger logger = LoggerFactory.getLogger(AdminSeed.class);

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    // Valores configuráveis via application.properties (com defaults)
    @Value("${admin.email:admin@homemedcare.com}")
    private String adminEmail;

    @Value("${admin.password:admin}")
    private String adminPassword;

    // If true, will reset existing admin password to the configured value
    @Value("${admin.reset-password:false}")
    private boolean resetPassword;

    public AdminSeed(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @PostConstruct
    public void init() {
        String email = adminEmail;

        Optional<Usuario> maybe = repo.findByEmail(email);
        if (maybe.isEmpty()) {
            Usuario u = new Usuario();
            u.setNome("Administrador");
            u.setEmail(email);
            u.setSenhaHash(encoder.encode(adminPassword));
            u.setRoles(Set.of(Role.ROLE_ADMIN));
            u.setAtivo(true);
            repo.save(u);
            logger.info("✅ ADMIN criado: {}", email);
        } else {
            Usuario existing = maybe.get();
            boolean changed = false;

            // Ensure roles contain ROLE_ADMIN
            Set<Role> roles = new HashSet<>(existing.getRoles() != null ? existing.getRoles() : Set.of());
            if (!roles.contains(Role.ROLE_ADMIN)) {
                roles.add(Role.ROLE_ADMIN);
                existing.setRoles(roles);
                changed = true;
            }

            // Ensure ativo is true
            // boolean primitive -> use isAtivo()
            if (!existing.isAtivo()) {
                existing.setAtivo(true);
                changed = true;
            }

            // Optionally reset password if configured
            if (resetPassword) {
                existing.setSenhaHash(encoder.encode(adminPassword));
                changed = true;
                logger.info("⚠️ admin.reset-password=true — senha do admin reiniciada para o valor configurado");
            }

            if (changed) {
                repo.save(existing);
                logger.info("🔁 ADMIN existente atualizado: {}", email);
            } else {
                logger.info("ℹ️ ADMIN já existente e consistente: {}", email);
            }
        }
    }
}
