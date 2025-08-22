package com.pmh.backendhomemedcare.service;

import com.pmh.backendhomemedcare.model.dto.in.InProfissionalDto;
import com.pmh.backendhomemedcare.model.dto.out.OutGenericStringDto;
import com.pmh.backendhomemedcare.model.dto.out.OutProfissionalDto;
import com.pmh.backendhomemedcare.model.entity.Endereco;
import com.pmh.backendhomemedcare.model.entity.Profissional;
import com.pmh.backendhomemedcare.model.factory.EnderecoFactory;
import com.pmh.backendhomemedcare.model.factory.ProfissionalFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.pmh.backendhomemedcare.repository.EnderecoRepo;
import com.pmh.backendhomemedcare.repository.ProfissionalRepo;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfissionalService {
    private final ProfissionalRepo repo;
    private final EnderecoRepo enderecoRepo;
    private final ProfissionalFactory profissionalFactory;
    private final EnderecoFactory enderecoFactory;

    public ProfissionalService(ProfissionalRepo repo, EnderecoRepo enderecoRepo, ProfissionalFactory profissionalFactory, EnderecoFactory enderecoFactory) {
        this.repo = repo;
        this.enderecoRepo = enderecoRepo;
        this.profissionalFactory = profissionalFactory;
        this.enderecoFactory = enderecoFactory;
    }


    public OutGenericStringDto create(InProfissionalDto profissional) {

        Endereco endereco = enderecoFactory.createEnderecoFromDto(profissional.inEnderecoDto());

        Endereco existingEndereco = enderecoRepo.findByEndereco(endereco)
                .orElseGet(() -> enderecoRepo.save(endereco));

        Profissional profissionalEntity = profissionalFactory.createProfissionalFromDto(profissional, existingEndereco);

        repo.save(profissionalEntity);

        return new OutGenericStringDto("Profissional cadastrado com sucesso!");
    }

    public Page<OutProfissionalDto> findAllPaginated(int page, int size, String sortBy, boolean desc) {
        Sort sort = desc ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return repo.findAll(pageable).map(profissionalFactory::toOutProfissionalDto);
    }

    public List<OutProfissionalDto> findAll() {
        return repo.findAll()
                .stream()
                .map(profissionalFactory::toOutProfissionalDto)
                .collect(Collectors.toList());
    }

    public OutProfissionalDto update(Long id, InProfissionalDto profissional) {
        Profissional existingProfissional = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        Endereco endereco = enderecoFactory.createEnderecoFromDto(profissional.inEnderecoDto());

        Endereco existingEndereco = enderecoRepo.findByEndereco(endereco)
                .orElseGet(() -> enderecoRepo.save(endereco));

        existingProfissional.setNome(profissional.nome());
        existingProfissional.setTelefone(profissional.telefone());
        existingProfissional.setDocumento(profissional.documento());
        existingProfissional.setOcupacao(profissional.ocupacao());
        existingProfissional.setEndereco(existingEndereco);

        repo.save(existingProfissional);

        return profissionalFactory.toOutProfissionalDto(existingProfissional);
    }

    public String delete(Long id) {
        Profissional profissional = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        repo.delete(profissional);
        return String.format("Profissional %s deletado com sucesso!", profissional.getNome());
    }
}
