package com.pmh.backendhomemedcare.controller;

import com.pmh.backendhomemedcare.model.dto.in.InAgendamentoDto;
import com.pmh.backendhomemedcare.model.dto.out.OutGenericStringDto;
import com.pmh.backendhomemedcare.model.dto.out.OutSugestaoAgendamentoDto;
import com.pmh.backendhomemedcare.service.AgendamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agendamento")
public class AgendamentoController {

    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    /**
     * Cria um novo agendamento confirmado.
     */
    @PostMapping("/agendar")
    public ResponseEntity<OutGenericStringDto> criarAgendamento(@RequestBody InAgendamentoDto dto) {
        return ResponseEntity.ok(service.criarAgendamento(dto));
    }

    /**
     * Simula horários disponíveis para um paciente/profissional.
     */
    @PostMapping("/simular")
    public ResponseEntity<List<OutSugestaoAgendamentoDto>> simularHorarios(@RequestBody InAgendamentoDto dto) {
        return ResponseEntity.ok(service.simularHorarios(dto));
    }
}
