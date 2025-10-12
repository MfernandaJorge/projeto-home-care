package com.pmh.backendhomemedcare.service;

import com.pmh.backendhomemedcare.model.dto.in.InPacienteDto;
import com.pmh.backendhomemedcare.model.dto.out.OutGenericStringDto;
import com.pmh.backendhomemedcare.model.dto.out.OutPacienteDto;
import com.pmh.backendhomemedcare.model.entity.Endereco;
import com.pmh.backendhomemedcare.model.entity.Paciente;
import com.pmh.backendhomemedcare.model.factory.EnderecoFactory;
import com.pmh.backendhomemedcare.model.factory.PacienteFactory;
import com.pmh.backendhomemedcare.repository.PacienteRepository;
import com.pmh.backendhomemedcare.repository.EnderecoRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PacienteService {
    private final PacienteRepository repository;
    private final EnderecoRepo enderecoRepo;
    private final PacienteFactory pacienteFactory;
    private final EnderecoFactory enderecoFactory;
    private final EnderecoService enderecoService;


    public PacienteService(PacienteRepository repository, EnderecoRepo enderecoRepo, PacienteFactory pacienteFactory,
                           EnderecoFactory enderecoFactory, EnderecoService enderecoService) {
        this.repository = repository;
        this.enderecoRepo = enderecoRepo;
        this.pacienteFactory = pacienteFactory;
        this.enderecoFactory = enderecoFactory;
        this.enderecoService = enderecoService;
    }

    public OutGenericStringDto create(InPacienteDto dto) {
        Endereco endereco = enderecoFactory.createEnderecoFromDto(dto.inEnderecoDto());

        Endereco existingEndereco = enderecoRepo.findByEndereco(endereco)
                .orElseGet(() -> enderecoService.getOrCreateEndereco(endereco));

        Paciente paciente = pacienteFactory.createPacienteFromDto(dto, existingEndereco);
        repository.save(paciente);

        return new OutGenericStringDto("Paciente criado com sucesso");
    }

    public Page<OutPacienteDto> findAllPaginated(int page, int size, String sortBy, boolean desc) {
        Sort sort = desc ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return repository.findAll(pageable).map(pacienteFactory::toOutPacienteDto);
    }

    public List<OutPacienteDto> findAll() {
        return repository.findAll()
                .stream()
                .map(pacienteFactory::toOutPacienteDto)
                .collect(Collectors.toList());
    }

    public OutPacienteDto update(Long id, InPacienteDto dto) {
        Paciente existingPaciente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente not found"));

        Endereco endereco = enderecoFactory.createEnderecoFromDto(dto.inEnderecoDto());
        Endereco existingEndereco = enderecoRepo.findByEndereco(endereco)
                .orElseGet(() -> enderecoRepo.save(endereco));

        existingPaciente.setNome(dto.nome());
        existingPaciente.setDocumento(dto.documento());
        existingPaciente.setEndereco(existingEndereco);
        existingPaciente.setDataNascimento(dto.dataNascimento());
        existingPaciente.setTelefone(dto.telefone());
        existingPaciente.setEmail(dto.email());

        repository.save(existingPaciente);

        return pacienteFactory.toOutPacienteDto(existingPaciente);
    }

    public String delete(Long id) {
        repository.deleteById(id);
        return "Paciente deletado com sucesso";
    }
}