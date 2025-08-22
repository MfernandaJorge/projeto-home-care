package com.pmh.backendhomemedcare.repository;

import com.pmh.backendhomemedcare.model.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnderecoRepo extends JpaRepository<Endereco, Long> {
    default Optional<Endereco> findByEndereco(Endereco endereco) {
        return findByLogradouroAndBairroAndCidadeAndEstadoAndCepAndNumero(
                endereco.getLogradouro(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getCep(),
                endereco.getNumero()
        );
    }

    Optional<Endereco> findByLogradouroAndBairroAndCidadeAndEstadoAndCepAndNumero(
            String logradouro, String bairro, String cidade, String estado, String cep, Integer numero);
}