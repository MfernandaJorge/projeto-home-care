package com.pmh.backendhomemedcare.service;

import com.pmh.backendhomemedcare.model.dto.in.InProfissionalDto;
import com.pmh.backendhomemedcare.model.dto.in.InJornadaDto;
import com.pmh.backendhomemedcare.model.dto.in.InJornadaDiaDto;
import com.pmh.backendhomemedcare.model.dto.in.InJornadaExcecaoDto;
import com.pmh.backendhomemedcare.model.dto.out.OutGenericStringDto;
import com.pmh.backendhomemedcare.model.dto.out.OutProfissionalDto;
import com.pmh.backendhomemedcare.model.entity.Endereco;
import com.pmh.backendhomemedcare.model.entity.JornadaDia;
import com.pmh.backendhomemedcare.model.entity.JornadaExcecao;
import com.pmh.backendhomemedcare.model.entity.JornadaProfissional;
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
import com.pmh.backendhomemedcare.repository.JornadaProfissionalRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfissionalService {
    private final ProfissionalRepo repo;
    private final EnderecoRepo enderecoRepo;
    private final ProfissionalFactory profissionalFactory;
    private final EnderecoFactory enderecoFactory;
    private final JornadaProfissionalRepo jornadaRepo;

    public ProfissionalService(ProfissionalRepo repo, EnderecoRepo enderecoRepo, ProfissionalFactory profissionalFactory, EnderecoFactory enderecoFactory, JornadaProfissionalRepo jornadaRepo) {
        this.repo = repo;
        this.enderecoRepo = enderecoRepo;
        this.profissionalFactory = profissionalFactory;
        this.enderecoFactory = enderecoFactory;
        this.jornadaRepo = jornadaRepo;
    }


    public OutGenericStringDto create(InProfissionalDto profissional) {

        Endereco endereco = enderecoFactory.createEnderecoFromDto(profissional.inEnderecoDto());

        Endereco existingEndereco = enderecoRepo.findByEndereco(endereco)
                .orElseGet(() -> enderecoRepo.save(endereco));

        Profissional profissionalEntity = profissionalFactory.createProfissionalFromDto(profissional, existingEndereco);

        // if frontend provided jornadaId, associate existing jornada template with this profissional
        if (profissional.jornadaId() != null) {
            Optional<JornadaProfissional> opt = jornadaRepo.findById(profissional.jornadaId());
            opt.ifPresent(profissionalEntity::setJornada);
        }

        // if frontend provided full jornada DTO, create a new jornada and associate
        if (profissional.jornadaDto() != null) {
            JornadaProfissional nova = createJornadaFromDto(profissional.jornadaDto());
            jornadaRepo.save(nova);
            profissionalEntity.setJornada(nova);
        }

        // save profissional (will persist jornada relationship)
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

        // update jornada if provided (by id)
        if (profissional.jornadaId() != null) {
            jornadaRepo.findById(profissional.jornadaId()).ifPresent(existingProfissional::setJornada);
        }

        // if frontend provided full jornada DTO, create new jornada and associate
        if (profissional.jornadaDto() != null) {
            JornadaProfissional nova = createJornadaFromDto(profissional.jornadaDto());
            jornadaRepo.save(nova);
            existingProfissional.setJornada(nova);
        }

        repo.save(existingProfissional);

        return profissionalFactory.toOutProfissionalDto(existingProfissional);
    }

    public String delete(Long id) {
        Profissional profissional = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        repo.delete(profissional);
        return String.format("Profissional %s deletado com sucesso!", profissional.getNome());
    }

    // helper to convert InJornadaDto into JornadaProfissional entity
    private JornadaProfissional createJornadaFromDto(InJornadaDto dto) {
        JornadaProfissional j = new JornadaProfissional();

        if (dto.diasSemana() != null) {
            List<JornadaDia> dias = dto.diasSemana().stream().map(d -> {
                JornadaDia jd = new JornadaDia();
                jd.setDiaSemana(d.diaSemana());
                jd.setInicio(d.inicio());
                jd.setFim(d.fim());
                jd.setInicioAlmoco(d.inicioAlmoco());
                jd.setFimAlmoco(d.fimAlmoco());
                jd.setTrabalha(d.trabalha());
                jd.setJornada(j);
                return jd;
            }).collect(Collectors.toList());
            j.setDiasSemana(dias);
        }

        if (dto.excecoes() != null) {
            List<JornadaExcecao> exs = dto.excecoes().stream().map(e -> {
                JornadaExcecao ex = new JornadaExcecao();
                ex.setData(e.data());
                ex.setFolga(e.folga());
                ex.setInicio(e.inicio());
                ex.setFim(e.fim());
                ex.setJornada(j);
                return ex;
            }).collect(Collectors.toList());
            j.setExcecoes(exs);
        }

        return j;
    }
}
