package com.pmh.backendhomemedcare.service;

import com.pmh.backendhomemedcare.model.dto.in.InAgendamentoDto;
import com.pmh.backendhomemedcare.model.dto.in.InCancelarAgendamentoDto;
import com.pmh.backendhomemedcare.model.dto.out.OutAtendimentoDiaDto;
import com.pmh.backendhomemedcare.model.dto.out.OutGenericStringDto;
import com.pmh.backendhomemedcare.model.dto.out.OutSugestaoAgendamentoDto;
import com.pmh.backendhomemedcare.model.entity.*;
import com.pmh.backendhomemedcare.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepo;
    private final PacienteRepository pacienteRepo;
    private final ProfissionalRepo profissionalRepo;
    private final TipoAtendimentoRepo tipoRepo;
    private final JornadaService jornadaService;

    public AgendamentoService(
            AgendamentoRepository agendamentoRepo,
            PacienteRepository pacienteRepo,
            ProfissionalRepo profissionalRepo,
            TipoAtendimentoRepo tipoRepo,
            JornadaService jornadaService
    ) {
        this.agendamentoRepo = agendamentoRepo;
        this.pacienteRepo = pacienteRepo;
        this.profissionalRepo = profissionalRepo;
        this.tipoRepo = tipoRepo;
        this.jornadaService = jornadaService;
    }

    @Transactional
    public OutGenericStringDto criarAgendamento(InAgendamentoDto dto) {
        if (dto == null)
            throw new IllegalArgumentException("DTO não pode ser nulo");

        if (dto.diaDesejado() == null || dto.horaDesejada() == null)
            throw new IllegalArgumentException("Data e hora do agendamento são obrigatórias");

        Paciente paciente = pacienteRepo.findById(dto.pacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        TipoAtendimento tipo = tipoRepo.findById(dto.tipoAtendimento())
                .orElseThrow(() -> new RuntimeException("Tipo de atendimento não encontrado"));

        Profissional profissional = null;
        if (dto.profissionalId() != null) {
            profissional = profissionalRepo.findById(dto.profissionalId())
                    .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        }

        LocalDateTime inicio = LocalDateTime.of(dto.diaDesejado(), dto.horaDesejada());
        LocalDateTime fim = inicio.plusMinutes(tipo.getDuracaoMinutos())
                                  .plusMinutes((long) paciente.getEndereco().getDurationMin());

        // 🔍 Verifica conflito se o profissional foi informado
        if (profissional != null) {
            boolean conflita = agendamentoRepo.existsConflitoCriacao(
                    profissional.getId(), inicio, fim
            );
            if (conflita) {
                throw new RuntimeException("Conflito de horário: o profissional já possui um agendamento nesse intervalo.");
            }
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setPaciente(paciente);
        agendamento.setProfissional(profissional);
        agendamento.setTipoAtendimento(tipo);
        agendamento.setInicio(inicio);
        agendamento.setFim(fim);

        agendamentoRepo.save(agendamento);

        return new OutGenericStringDto("✅ Agendamento criado com sucesso para " +
                paciente.getNome() + " em " +
                dto.diaDesejado() + " às " + dto.horaDesejada() +
                (profissional != null ? " com " + profissional.getNome() : ""));
    }


    public List<OutSugestaoAgendamentoDto> simularHorarios(InAgendamentoDto dto) {
        SimulacaoContexto dadosParaSimulacao = prepararContexto(dto);

        Map<Long, JornadaProfissional> jornadas = carregarJornadas(dadosParaSimulacao.profissionais());

        return dadosParaSimulacao.profissionais().stream()
                .flatMap(prof -> gerarSugestoesParaProfissional(prof, dadosParaSimulacao, jornadas.get(prof.getId())).stream())
                .collect(Collectors.toList());
    }


    private SimulacaoContexto prepararContexto(InAgendamentoDto dto) {
        Objects.requireNonNull(dto, "DTO não pode ser nulo");

        Paciente paciente = pacienteRepo.findById(dto.pacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        TipoAtendimento tipo = tipoRepo.findById(dto.tipoAtendimento())
                .orElseThrow(() -> new RuntimeException("Tipo de atendimento não encontrado"));

        List<Profissional> profissionais = (dto.profissionalId() != null)
                ? List.of(profissionalRepo.findById(dto.profissionalId())
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado")))
                : profissionalRepo.findAll();

        int dias = (dto.diasSimulacao() != null && dto.diasSimulacao() > 0) ? dto.diasSimulacao() : 1;

        LocalDateTime referencia = LocalDateTime.of(
                Optional.ofNullable(dto.diaDesejado()).orElse(LocalDate.now()),
                Optional.ofNullable(dto.horaDesejada()).orElse(LocalTime.now().plusHours(1).withMinute(0))
        );

        double deslocamento = paciente.getEndereco() != null ? paciente.getEndereco().getDurationMin() : 0d;

        return new SimulacaoContexto(paciente, tipo, profissionais, dias, referencia, deslocamento);
    }

    private Map<Long, JornadaProfissional> carregarJornadas(List<Profissional> profissionais) {
        Map<Long, JornadaProfissional> jornadas = new HashMap<>();
        for (Profissional p : profissionais) {
            var j = jornadaService.obterJornadaCompleta(p.getId());
            if (j != null) {
                if (j.getExcecoes() != null) j.getExcecoes().size(); // força carga
                jornadas.put(p.getId(), j);
            }
        }
        return jornadas;
    }

    private List<OutSugestaoAgendamentoDto> gerarSugestoesParaProfissional(
            Profissional prof,
            SimulacaoContexto ctx,
            JornadaProfissional jornada
    ) {
        if (jornada == null) return Collections.emptyList();

        List<OutSugestaoAgendamentoDto> sugestoes = new ArrayList<>();

        LocalDate dataInicial = ctx.referencia().toLocalDate();
        LocalDate dataFinal = dataInicial.plusDays(ctx.dias() - 1);

        List<Agendamento> agenda = agendamentoRepo.findAtivosByProfissionalIdAndInicioBetween(
                prof.getId(),
                dataInicial.atStartOfDay(),
                dataFinal.atTime(LocalTime.MAX)
        );

        for (int d = 0; d < ctx.dias(); d++) {
            LocalDate data = dataInicial.plusDays(d);
            var disp = jornadaService.consultarDisponibilidadeLocal(jornada, data);
            if (disp.inicio() == null || disp.fim() == null) continue;

            var janela = calcularJanela(ctx.referencia().toLocalTime(), disp.inicio(), disp.fim());

            gerarHorariosDisponiveis(prof, data, ctx, agenda, janela, sugestoes);
        }

        return sugestoes;
    }

    private void gerarHorariosDisponiveis(
            Profissional prof,
            LocalDate data,
            SimulacaoContexto ctx,
            List<Agendamento> agenda,
            TimeRange janela,
            List<OutSugestaoAgendamentoDto> sugestoes
    ) {
        final int duracao = ctx.tipo().getDuracaoMinutos();
        final long acrescimo = (long) (ctx.deslocamento() * 2);

        for (LocalTime hora = janela.inicio(); !hora.isAfter(janela.fim()); hora = hora.plusMinutes(15)) {
            LocalDateTime ini = LocalDateTime.of(data, hora);
            LocalDateTime fim = ini.plusMinutes(duracao + acrescimo);

            boolean conflita = agenda.stream()
                    .anyMatch(a -> !(a.getFim().isBefore(ini) || a.getInicio().isAfter(fim)));

            if (!conflita) {
                sugestoes.add(new OutSugestaoAgendamentoDto(
                        prof.getNome(),
                        data,
                        hora,
                        duracao + acrescimo
                ));
            }
        }
    }

    private TimeRange calcularJanela(LocalTime horaRef, LocalTime inicioDisp, LocalTime fimDisp) {
        LocalTime inicioJanela = horaRef.minusMinutes(30);
        LocalTime fimJanela = horaRef.plusMinutes(30);

        if (inicioJanela.isBefore(inicioDisp)) {
            inicioJanela = inicioDisp;
        }

        if (fimJanela.isAfter(fimDisp.plusMinutes(30))) {
            fimJanela = fimDisp.plusMinutes(30);
        }

        return new TimeRange(inicioJanela, fimJanela);
    }

    @Transactional
    public OutGenericStringDto cancelarAgendamento(InCancelarAgendamentoDto dto) {
        if (dto == null || dto.agendamentoId() == null) {
            throw new IllegalArgumentException("Dados de cancelamento inválidos");
        }

        Agendamento ag = agendamentoRepo.findById(dto.agendamentoId())
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        if (ag.isCancelado()) {
            return new OutGenericStringDto("Agendamento " + ag.getId() + " já está cancelado.");
        }
        if (ag.isConcluido()) {
            throw new RuntimeException("Não é possível cancelar um agendamento já concluído.");
        }

        ag.setCancelado(true);
        ag.setCanceladoEm(LocalDateTime.now());                 // ✅ registra o horário
        if (dto.motivo() != null && !dto.motivo().isBlank()) {  // ✅ registra o motivo (opcional)
            ag.setMotivoCancelamento(dto.motivo().trim());
        }

        agendamentoRepo.save(ag);

        return new OutGenericStringDto(
                "✅ Agendamento " + ag.getId() + " cancelado" +
                        (ag.getMotivoCancelamento() != null ? " (motivo: " + ag.getMotivoCancelamento() + ")" : "") +
                        " às " + ag.getCanceladoEm() + "."
        );
    }

    @Transactional
    public OutGenericStringDto marcarConcluido(Long id) {
        if (id == null) throw new IllegalArgumentException("ID do agendamento é obrigatório");

        Agendamento ag = agendamentoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        if (ag.isCancelado()) {
            throw new RuntimeException("Não é possível concluir um agendamento cancelado.");
        }
        if (ag.isConcluido()) {
            return new OutGenericStringDto("Agendamento " + ag.getId() + " já está concluído.");
        }

        ag.setConcluido(true);
        agendamentoRepo.save(ag);

        return new OutGenericStringDto("✅ Agendamento " + ag.getId() + " marcado como concluído.");
    }

    public List<OutAtendimentoDiaDto> listarNaoCanceladosDoDia(LocalDate dia) {
        if (dia == null) dia = LocalDate.now();
        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fim = dia.atTime(LocalTime.MAX);
        return agendamentoRepo.listarNaoCanceladosNoPeriodo(inicio, fim);
    }

    public List<OutAtendimentoDiaDto> listarTodosDoDia(LocalDate dia) {
        if (dia == null) dia = LocalDate.now();
        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fim = dia.atTime(LocalTime.MAX);
        return agendamentoRepo.listarTodosNoPeriodo(inicio, fim);
    }


    // ============================================================
    // 🔸 Records auxiliares (estruturas imutáveis internas)
    // ============================================================

    private record TimeRange(LocalTime inicio, LocalTime fim) {}

    private record SimulacaoContexto(
            Paciente paciente,
            TipoAtendimento tipo,
            List<Profissional> profissionais,
            int dias,
            LocalDateTime referencia,
            double deslocamento
    ) {}
}
