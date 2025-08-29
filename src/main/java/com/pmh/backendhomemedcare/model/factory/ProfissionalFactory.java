package com.pmh.backendhomemedcare.model.factory;

import com.pmh.backendhomemedcare.model.dto.in.InProfissionalDto;
import com.pmh.backendhomemedcare.model.dto.out.OutEnderecoDto;
import com.pmh.backendhomemedcare.model.dto.out.OutProfissionalDto;
import com.pmh.backendhomemedcare.model.entity.Endereco;
import com.pmh.backendhomemedcare.model.entity.Profissional;
import org.springframework.stereotype.Component;

@Component
public class ProfissionalFactory {
    private final EnderecoFactory enderecoFactory;

    public ProfissionalFactory(EnderecoFactory enderecoFactory) {
        this.enderecoFactory = enderecoFactory;
    }

    public Profissional createProfissionalFromDto(InProfissionalDto inProfissionalDto, Endereco endereco) {
        Profissional profissional = new Profissional();
        profissional.setNome(inProfissionalDto.nome());
        profissional.setDocumento(inProfissionalDto.documento());
        profissional.setEndereco(endereco);
        profissional.setOcupacao(inProfissionalDto.ocupacao());
        profissional.setTelefone(inProfissionalDto.telefone());
        return profissional;
    }

    public OutProfissionalDto toOutProfissionalDto(Profissional profissional) {
        return new OutProfissionalDto(
                profissional.getId(),
                profissional.getNome(),
                profissional.getDocumento(),
                enderecoFactory.toOutEnderecoDto(profissional.getEndereco()),
                profissional.getOcupacao(),
                profissional.getTelefone()
        );
    }
}
