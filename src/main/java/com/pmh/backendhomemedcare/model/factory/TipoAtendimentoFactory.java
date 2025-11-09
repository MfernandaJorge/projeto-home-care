package com.pmh.backendhomemedcare.model.factory;

import com.pmh.backendhomemedcare.model.dto.out.OutTipoAtendimentoDto;
import com.pmh.backendhomemedcare.model.entity.TipoAtendimento;
import org.springframework.stereotype.Component;

@Component
public class TipoAtendimentoFactory {

    public OutTipoAtendimentoDto toOutDto(TipoAtendimento entity) {
        return new OutTipoAtendimentoDto(
                entity.getId(),
                entity.getDescricao(),
                entity.getDuracaoMinutos(),
                entity.isAtivo()
        );
    }
}
