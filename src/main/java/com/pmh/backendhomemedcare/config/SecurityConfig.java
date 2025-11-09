package com.pmh.backendhomemedcare.config;

import com.pmh.backendhomemedcare.security.JwtAuthenticationFilter;
import com.pmh.backendhomemedcare.security.JwtService;
import com.pmh.backendhomemedcare.security.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtService jwtService,
                                           UsuarioDetailsService uds) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Habilita o CORS dentro do Spring Security
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Permitir acesso aos arquivos estáticos (frontend)
                        .requestMatchers("/", "/index.html", "/favicon.ico", "/manifest.json", "/robots.txt", "/logo*.png").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/media/**", "/static/**").permitAll()
                        // Permitir acesso ao console H2 (apenas desenvolvimento)
                        .requestMatchers("/h2-console/**").permitAll()
                        // Permitir acesso ao endpoint de login (com e sem /api)
                        .requestMatchers("/auth/login", "/api/auth/login").permitAll()
                        // Restringir acesso admin
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Todas as outras requisições que começam com /api precisam de autenticação
                        .requestMatchers("/api/**").authenticated()
                        // Permitir acesso a tudo que não seja API (para o React Router funcionar)
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())) // Necessário para o H2 console funcionar
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, uds),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite o frontend tanto em desenvolvimento (3000) quanto servido pelo Spring Boot (8080)
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // necessário para enviar cookies ou headers Authorization

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
