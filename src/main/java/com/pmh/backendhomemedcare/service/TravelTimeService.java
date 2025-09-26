package com.pmh.backendhomemedcare.service;

import com.pmh.backendhomemedcare.exception.InvalidRequestException;
import com.pmh.backendhomemedcare.model.dto.in.InTravelRequestDto;
import com.pmh.backendhomemedcare.model.dto.out.OutTravelDto;
import com.pmh.backendhomemedcare.model.dto.RouteResult;
import org.springframework.stereotype.Service;

@Service
public class TravelTimeService {

    private final GeocodingService geocodingService;
    private final RouteService routeService;

    public TravelTimeService(GeocodingService geocodingService, RouteService routeService) {
        this.geocodingService = geocodingService;
        this.routeService = routeService;
    }

    /**
     * Calcula distância e duração entre dois endereços.
     *
     * @param request DTO com endereço de origem e destino
     * @return OutTravelDto com distância (km) e duração (min)
     */
    public OutTravelDto calcularTempo(InTravelRequestDto request) {
        // Validação simples
        if (request.origemEndereco() == null || request.origemEndereco().isBlank()) {
            throw new InvalidRequestException("Endereço de origem não pode estar vazio.");
        }
        if (request.destinoEndereco() == null || request.destinoEndereco().isBlank()) {
            throw new InvalidRequestException("Endereço de destino não pode estar vazio.");
        }

        // Geocoding
        double[] origemCoords = geocodingService.getCoordinates(request.origemEndereco());
        double[] destinoCoords = geocodingService.getCoordinates(request.destinoEndereco());

        // Calcular rota
        RouteResult routeResult = routeService.calcularRota(
                origemCoords[0], origemCoords[1],
                destinoCoords[0], destinoCoords[1]
        );

        // Extrair distância e duração
        double distancia = routeResult.routes().getFirst().summary().distance() / 1000.0; // metros → km
        double duracao = routeResult.routes().getFirst().summary().duration() / 60.0;     // segundos → minutos

        return new OutTravelDto(distancia, duracao);
    }
}
