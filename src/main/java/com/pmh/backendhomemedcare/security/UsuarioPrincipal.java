package com.pmh.backendhomemedcare.security;

import com.pmh.backendhomemedcare.model.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public record UsuarioPrincipal(Usuario usuario) implements org.springframework.security.core.userdetails.UserDetails {

    @Override
    public Set<? extends GrantedAuthority> getAuthorities() {
        return usuario.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.name()))
                .collect(Collectors.toSet());
    }

    @Override public String getPassword() { return usuario.getSenhaHash(); }
    @Override public String getUsername() { return usuario.getEmail(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return usuario.isAtivo(); }
}
