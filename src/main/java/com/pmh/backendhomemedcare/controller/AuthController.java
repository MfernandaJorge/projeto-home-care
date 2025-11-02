package com.pmh.backendhomemedcare.controller;

import com.pmh.backendhomemedcare.model.dto.in.InLoginDto;
import com.pmh.backendhomemedcare.model.dto.out.OutAuthDto;
import com.pmh.backendhomemedcare.security.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/login")
    public ResponseEntity<OutAuthDto> login(@RequestBody InLoginDto dto) {
        return ResponseEntity.ok(auth.login(dto));
    }
}
