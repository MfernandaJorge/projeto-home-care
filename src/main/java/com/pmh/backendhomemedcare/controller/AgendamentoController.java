package com.pmh.backendhomemedcare.controller;

import com.pmh.backendhomemedcare.model.dto.in.InAgendamentoDto;
import com.pmh.backendhomemedcare.model.dto.in.InCancelarAgendamentoDto;
import com.pmh.backendhomemedcare.model.dto.out.OutAtendimentoDiaDto;
import com.pmh.backendhomemedcare.model.dto.out.OutGenericStringDto;
import com.pmh.backendhomemedcare.model.dto.out.OutSugestaoAgendamentoDto;
import com.pmh.backendhomemedcare.service.AgendamentoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/api/agendamento")
public class AgendamentoController {

    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    @PostMapping("/agendar")
    public ResponseEntity<OutGenericStringDto> criarAgendamento(@RequestBody InAgendamentoDto dto) {
        return ResponseEntity.ok(service.criarAgendamento(dto));
    }

    @PostMapping("/simular")
    public ResponseEntity<List<OutSugestaoAgendamentoDto>> simularHorarios(@RequestBody InAgendamentoDto dto) {
        return ResponseEntity.ok(service.simularHorarios(dto));
    }

    @PostMapping("/cancelar")
    public ResponseEntity<OutGenericStringDto> cancelar(@RequestBody InCancelarAgendamentoDto dto) {
        OutGenericStringDto resp = service.cancelarAgendamento(dto);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/concluir/{id}")
    public ResponseEntity<OutGenericStringDto> concluir(@PathVariable Long id) {
        OutGenericStringDto resp = service.marcarConcluido(id);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/agendados")
    public ResponseEntity<List<OutAtendimentoDiaDto>> listarAgendados(
            @RequestParam(name = "dia", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dia
    ) {
        var lista = service.listarTodosDoDia(dia);
        return ResponseEntity.ok(lista);
    }
}
