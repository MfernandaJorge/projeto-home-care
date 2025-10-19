package com.pmh.backendhomemedcare.model.factory;

import com.pmh.backendhomemedcare.model.dto.in.InEnderecoDto;
import com.pmh.backendhomemedcare.model.dto.out.OutEnderecoDto;
import com.pmh.backendhomemedcare.model.entity.Endereco;
import org.springframework.stereotype.Component;

@Component
public class EnderecoFactory {

    public Endereco createEnderecoFromDto(InEnderecoDto inEnderecoDto) {
        Endereco endereco = new Endereco();
        endereco.setLogradouro(inEnderecoDto.logradouro());
        endereco.setBairro(inEnderecoDto.bairro());
        endereco.setCidade(inEnderecoDto.cidade());
        endereco.setEstado(inEnderecoDto.estado());
        endereco.setCep(inEnderecoDto.cep());
        endereco.setNumero(inEnderecoDto.numero());
        return endereco;
    }

    public OutEnderecoDto toOutEnderecoDto(Endereco endereco) {
        return new OutEnderecoDto(
                endereco.getLogradouro(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getCep(),
                endereco.getNumero(),
                endereco.getDistanceKm(),
                endereco.getDurationMin(),
                endereco.getLatitude(),
                endereco.getLongitude()
        );
    }
}
