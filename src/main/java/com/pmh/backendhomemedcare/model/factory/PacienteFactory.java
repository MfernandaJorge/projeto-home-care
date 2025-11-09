package com.pmh.backendhomemedcare.model.factory;

import com.pmh.backendhomemedcare.model.dto.in.InPacienteDto;
import com.pmh.backendhomemedcare.model.dto.out.OutPacienteDto;
import com.pmh.backendhomemedcare.model.entity.Endereco;
import com.pmh.backendhomemedcare.model.entity.Paciente;
import org.springframework.stereotype.Component;

@Component
public class PacienteFactory {

    private final EnderecoFactory enderecoFactory;

    public PacienteFactory(EnderecoFactory enderecoFactory) {
        this.enderecoFactory = enderecoFactory;
    }

    public Paciente createPacienteFromDto(InPacienteDto inPacienteDto, Endereco endereco) {
        Paciente paciente = new Paciente();
        paciente.setNome(inPacienteDto.nome());
        paciente.setDocumento(inPacienteDto.documento());
        paciente.setEndereco(endereco);
        paciente.setDataNascimento(inPacienteDto.dataNascimento());
        paciente.setTelefone(inPacienteDto.telefone());
        paciente.setEmail(inPacienteDto.email());
        return paciente;
    }

    public OutPacienteDto toOutPacienteDto(Paciente paciente) {
        return new OutPacienteDto(
                paciente.getId(),
                paciente.getNome(),
                paciente.getDocumento(),
                enderecoFactory.toOutEnderecoDto(paciente.getEndereco()),
                paciente.getDataNascimento(),
                paciente.getTelefone(),
                paciente.getEmail()
        );
    }
}