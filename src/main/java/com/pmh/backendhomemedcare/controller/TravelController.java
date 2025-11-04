package com.pmh.backendhomemedcare.controller;

import com.pmh.backendhomemedcare.model.dto.in.InTravelRequestDto;
import com.pmh.backendhomemedcare.model.dto.out.OutTravelDto;
import com.pmh.backendhomemedcare.service.TravelTimeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {
        "http://localhost:3000",           // ambiente de desenvolvimento
        "https://sistema.homemedcare.com.br" // domínio de produção
})
@RestController
@RequestMapping("/api/travel")
public class TravelController {

    private final TravelTimeService travelTimeService;

    public TravelController(TravelTimeService travelTimeService) {
        this.travelTimeService = travelTimeService;
    }

    /**
     * Calcula distância e duração entre dois endereços.
     * <p>
     * Exemplo de requisição POST:
     * {
     *   "origemEndereco": "Rua A, 123, Cidade",
     *   "destinoEndereco": "Rua B, 456, Cidade"
     * }
     *
     * @param request DTO de entrada
     * @return DTO de saída com distância (km) e duração (min)
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public OutTravelDto calcularTempo(@RequestBody InTravelRequestDto request) {
        return travelTimeService.calcularTempo(request);
    }
}
