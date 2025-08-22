package com.pmh.backendhomemedcare.model.dto.in;

public record InEnderecoDto(String logradouro,
                            String bairro,
                            String cidade,
                            String estado,
                            String cep,
                            int numero) {

}
