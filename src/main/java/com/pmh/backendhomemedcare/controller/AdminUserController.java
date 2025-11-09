package com.pmh.backendhomemedcare.controller;

import com.pmh.backendhomemedcare.model.dto.in.InCriarUsuarioDto;
import com.pmh.backendhomemedcare.security.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService service;

    public AdminUserController(AdminUserService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Long> criar(@RequestBody InCriarUsuarioDto dto) {
        Long id = service.criarUsuario(dto);
        return ResponseEntity.ok(id);
    }
}
