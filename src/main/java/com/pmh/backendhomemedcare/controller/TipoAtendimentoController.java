package com.pmh.backendhomemedcare.controller;

import com.pmh.backendhomemedcare.model.dto.out.OutTipoAtendimentoDto;
import com.pmh.backendhomemedcare.service.TipoAtendimentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/api/tipo-atendimento")
public class TipoAtendimentoController {

    private final TipoAtendimentoService service;

    public TipoAtendimentoController(TipoAtendimentoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<OutTipoAtendimentoDto>> listarTodos() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OutTipoAtendimentoDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
