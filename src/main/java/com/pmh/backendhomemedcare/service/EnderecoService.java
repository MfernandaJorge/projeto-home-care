package com.pmh.backendhomemedcare.service;

import com.pmh.backendhomemedcare.model.dto.in.InTravelRequestDto;
import com.pmh.backendhomemedcare.model.entity.Endereco;
import com.pmh.backendhomemedcare.repository.EnderecoRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EnderecoService {

    @Value("${clinica.endereco}")
    private String enderecoClinica;

    private final EnderecoRepo enderecoRepo;
    private final TravelTimeService travelTimeService;
    private final GeocodingService geocodingService;

    public EnderecoService(EnderecoRepo enderecoRepo,
                           TravelTimeService travelTimeService,
                           GeocodingService geocodingService) {
        this.enderecoRepo = enderecoRepo;
        this.travelTimeService = travelTimeService;
        this.geocodingService = geocodingService;
    }

    public Endereco getOrCreateEndereco(Endereco endereco) {
        return enderecoRepo.findByEndereco(endereco)
                .orElseGet(() -> {
                    try {
                        // obtem coordenadas apenas se ainda não existirem
                        if (endereco.getLatitude() == null || endereco.getLongitude() == null) {
                            var coords = geocodingService.getCoordinates(
                                    endereco.getLogradouro() + ", " + endereco.getNumero() + ", " + endereco.getCidade());
                            endereco.setLatitude(coords[0]);
                            endereco.setLongitude(coords[1]);
                            endereco.setGeocodedAt(LocalDateTime.now());
                        }

                        var travelDto = travelTimeService.calcularTempo(new InTravelRequestDto(
                                enderecoClinica,
                                endereco.getLogradouro() + ", " + endereco.getNumero() + ", " + endereco.getCidade(),
                                "driving-car"
                        ));

                        endereco.setDistanceKm(travelDto.distanceKm());
                        endereco.setDurationMin((long) travelDto.durationMin());

                    } catch (Exception e) {
                        System.err.println("⚠️ Falha ao processar endereço: " + e.getMessage());
                        endereco.setDistanceKm(-1);
                        endereco.setDurationMin(-1);
                    }

                    return enderecoRepo.save(endereco);
                });
    }
}
