package com.pmh.backendhomemedcare.model.factory;

import com.pmh.backendhomemedcare.model.dto.in.InEnderecoDto;
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
}
