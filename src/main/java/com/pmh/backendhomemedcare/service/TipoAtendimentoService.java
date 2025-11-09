package com.pmh.backendhomemedcare.service;

import com.pmh.backendhomemedcare.exception.InvalidRequestException;
import com.pmh.backendhomemedcare.model.dto.out.OutTipoAtendimentoDto;
import com.pmh.backendhomemedcare.model.factory.TipoAtendimentoFactory;
import com.pmh.backendhomemedcare.repository.TipoAtendimentoRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoAtendimentoService {

    private final TipoAtendimentoRepo repo;
    private final TipoAtendimentoFactory factory;

    public TipoAtendimentoService(TipoAtendimentoRepo repo, TipoAtendimentoFactory factory) {
        this.repo = repo;
        this.factory = factory;
    }

    public List<OutTipoAtendimentoDto> listAll() {
        return repo.findAll().stream().map(factory::toOutDto).toList();
    }

    public OutTipoAtendimentoDto findById(Long id) {
        var entity = repo.findById(id)
                .orElseThrow(() -> new InvalidRequestException("Tipo de atendimento não encontrado: " + id));
        return factory.toOutDto(entity);
    }
}
