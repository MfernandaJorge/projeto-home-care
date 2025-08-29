package com.pmh.backendhomemedcare.controller;

import com.pmh.backendhomemedcare.model.dto.out.OutGenericStringDto;
import com.pmh.backendhomemedcare.model.dto.out.OutProfissionalDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pmh.backendhomemedcare.service.ProfissionalService;
import com.pmh.backendhomemedcare.model.dto.in.InProfissionalDto;

import java.util.List;

@RestController
@RequestMapping("/profissional")
public class ProfissionalController {
    private ProfissionalService service;

    public ProfissionalController(ProfissionalService service) {
        this.service = service;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<OutGenericStringDto> create(@RequestBody InProfissionalDto profissional) {
        return ResponseEntity.ok(service.create(profissional));
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<OutProfissionalDto>> findAllPaginated(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size,
                                                                     @RequestParam(defaultValue = "id") String sortBy,
                                                                     @RequestParam(defaultValue = "false") boolean desc) {
        return ResponseEntity.ok(service.findAllPaginated(page, size, sortBy, desc));
    }

    @GetMapping("/all")
    public ResponseEntity<List<OutProfissionalDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<OutProfissionalDto> update(@PathVariable Long id, @RequestBody InProfissionalDto profissional) {
        return ResponseEntity.ok(service.update(id, profissional));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}
