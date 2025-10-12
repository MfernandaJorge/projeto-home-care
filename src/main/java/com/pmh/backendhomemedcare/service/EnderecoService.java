package com.pmh.backendhomemedcare.service;

import com.pmh.backendhomemedcare.model.dto.in.InTravelRequestDto;
import com.pmh.backendhomemedcare.model.entity.Endereco;
import com.pmh.backendhomemedcare.repository.EnderecoRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EnderecoService {

    @Value("${clinica.endereco}")
    private String enderecoClinica;

    private final EnderecoRepo enderecoRepo;
    private final TravelTimeService travelTimeService;

    public EnderecoService(EnderecoRepo enderecoRepo, TravelTimeService travelTimeService) {
        this.enderecoRepo = enderecoRepo;
        this.travelTimeService = travelTimeService;
    }

    public Endereco getOrCreateEndereco(Endereco endereco) {
        return enderecoRepo.findByEndereco(endereco)
                .orElseGet(() -> {
                    try {
                        var travelDto = travelTimeService.calcularTempo(new InTravelRequestDto(
                                enderecoClinica,
                                endereco.getLogradouro() + ", " + endereco.getNumero() + ", " + endereco.getCidade(),
                                "driving-car"
                        ));
                        endereco.setDistanceKm(travelDto.distanceKm());
                        endereco.setDurationMin(travelDto.durationMin());
                    } catch (Exception e) {
                        System.err.println("⚠️ Falha ao calcular distância: " + e.getMessage());
                        endereco.setDistanceKm(-1);
                        endereco.setDurationMin(-1);
                    }
                    enderecoRepo.save(endereco);
                    return endereco;
                });
    }
}