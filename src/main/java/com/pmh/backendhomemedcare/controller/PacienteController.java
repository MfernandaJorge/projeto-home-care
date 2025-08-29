package com.pmh.backendhomemedcare.controller;

import com.pmh.backendhomemedcare.model.dto.in.InPacienteDto;
import com.pmh.backendhomemedcare.model.dto.out.OutGenericStringDto;
import com.pmh.backendhomemedcare.model.dto.out.OutPacienteDto;
import com.pmh.backendhomemedcare.service.PacienteService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/paciente")
public class PacienteController {
    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<OutGenericStringDto> create(@RequestBody InPacienteDto paciente) {
        return ResponseEntity.ok(service.create(paciente));
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<OutPacienteDto>> findAllPaginated(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "id") String sortBy,
                                                                 @RequestParam(defaultValue = "false") boolean desc) {
        return ResponseEntity.ok(service.findAllPaginated(page, size, sortBy, desc));
    }

    @GetMapping("/all")
    public ResponseEntity<List<OutPacienteDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<OutPacienteDto> update(@PathVariable Long id, @RequestBody InPacienteDto paciente) {
        return ResponseEntity.ok(service.update(id, paciente));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}
