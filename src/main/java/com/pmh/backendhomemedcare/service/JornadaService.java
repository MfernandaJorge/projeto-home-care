package com.pmh.backendhomemedcare.service;

import com.pmh.backendhomemedcare.model.dto.out.DisponibilidadeDia;
import com.pmh.backendhomemedcare.model.entity.*;
import com.pmh.backendhomemedcare.model.enums.TipoDisponibilidade;
import com.pmh.backendhomemedcare.repository.JornadaProfissionalRepo;
import com.pmh.backendhomemedcare.repository.ProfissionalRepo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.util.Optional;

@Service
@Slf4j
public class JornadaService {

    private final ProfissionalRepo profissionalRepo;

    public JornadaService(JornadaProfissionalRepo jornadaRepo, ProfissionalRepo profissionalRepo) {
        this.profissionalRepo = profissionalRepo;
    }

    /**
     * 🔹 Recupera a jornada completa de um profissional
     * (usada para evitar consultas repetidas no banco durante simulações).
     */

    @Cacheable("jornadas")
    public JornadaProfissional obterJornadaCompleta(Long profissionalId) {
        Profissional p = profissionalRepo.findById(profissionalId)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado: " + profissionalId));
        JornadaProfissional j = p.getJornada();
        if (j == null) throw new RuntimeException("Jornada não encontrada para o profissional: " + profissionalId);
        // ensure dias are loaded (optional)
        j.getDiasSemana();
        return j;
    }

    /**
     * 🔹 Consulta disponibilidade usando jornada já carregada (sem nova query).
     */
    public DisponibilidadeDia consultarDisponibilidadeLocal(JornadaProfissional jornada, LocalDate data) {
        // default behavior: choose segment by mid-day reference
        return consultarDisponibilidadeLocal(jornada, data, LocalTime.NOON);
    }

    /**
     * Overload que recebe uma hora de referência para escolher corretamente
     * entre a parte noturna (inicio..23:59) ou a parte matutina (00:00..fim)
     * quando um turno atravessa a meia-noite.
     */
    public DisponibilidadeDia consultarDisponibilidadeLocal(JornadaProfissional jornada, LocalDate data, LocalTime referencia) {

        // 1️⃣ Verifica exceções (folga ou plantão)
        Optional<JornadaExcecao> excecao = jornada.getExcecoes()
                .stream()
                .filter(e -> e.getData().equals(data))
                .findFirst();

        if (excecao.isPresent()) {
            JornadaExcecao ex = excecao.get();
            if (ex.isFolga()) {
                return new DisponibilidadeDia(data, null, null, TipoDisponibilidade.FOLGA);
            } else {
                return new DisponibilidadeDia(data, ex.getInicio(), ex.getFim(), TipoDisponibilidade.PLANTAO);
            }
        }

        // 2️⃣ Aplica a regra padrão do dia da semana
        DayOfWeek diaSemana = data.getDayOfWeek();
        Optional<JornadaDia> dia = jornada.getDiasSemana()
                .stream()
                .filter(d -> d.getDiaSemana() == diaSemana)
                .findFirst();

        DayOfWeek diaAnterior = data.minusDays(1).getDayOfWeek();
        Optional<JornadaDia> diaPrev = jornada.getDiasSemana()
                .stream()
                .filter(d -> d.getDiaSemana() == diaAnterior)
                .findFirst();

        boolean prevWrap = diaPrev.isPresent() && diaPrev.get().isTrabalha() && diaPrev.get().getInicio() != null && diaPrev.get().getFim() != null && diaPrev.get().getFim().isBefore(diaPrev.get().getInicio());
        boolean thisWrap = dia.isPresent() && dia.get().isTrabalha() && dia.get().getInicio() != null && dia.get().getFim() != null && dia.get().getFim().isBefore(dia.get().getInicio());

        // If neither works and day is empty or not working => folga
        if ((dia.isEmpty() || !dia.get().isTrabalha()) && !prevWrap) {
            return new DisponibilidadeDia(data, null, null, TipoDisponibilidade.FOLGA);
        }

        // If both previous day and this day wrap (typical for night shifts), choose segment based on reference time
        if (prevWrap && thisWrap) {
            // morning segment from previous day's spill: 00:00 .. fimPrev
            LocalTime fimPrev = diaPrev.get().getFim();
            // evening segment from today's start: inicioThis .. 23:59
            LocalTime inicioThis = dia.get().getInicio();

            // choose based on referencia: if reference is in the evening choose evening segment, if early morning choose morning
            if (referencia != null && (referencia.isAfter(LocalTime.of(12, 0)) || referencia.equals(LocalTime.of(12,0)))) {
                return new DisponibilidadeDia(data, inicioThis, LocalTime.MAX, TipoDisponibilidade.NORMAL);
            } else {
                return new DisponibilidadeDia(data, LocalTime.MIDNIGHT, fimPrev, TipoDisponibilidade.NORMAL);
            }
        }

        // if previous day wraps and this day doesn't work, return morning part
        if (prevWrap) {
            log.debug("[jornada] prevWrap true e dia atual nao trabalha: retornando MIDNIGHT..{}", diaPrev.get().getFim());
            return new DisponibilidadeDia(data, LocalTime.MIDNIGHT, diaPrev.get().getFim(), TipoDisponibilidade.NORMAL);
        }

        if (dia.isEmpty() || !dia.get().isTrabalha()) {
            return new DisponibilidadeDia(data, null, null, TipoDisponibilidade.FOLGA);
        }

        JornadaDia jd = dia.get();
        // if the day's shift spills to next day, expose the part that belongs to this calendar date (inicio .. 23:59:59.999)
        if (jd.getInicio() != null && jd.getFim() != null && jd.getFim().isBefore(jd.getInicio())) {
            return new DisponibilidadeDia(data, jd.getInicio(), LocalTime.MAX, TipoDisponibilidade.NORMAL);
        }

        return new DisponibilidadeDia(data, jd.getInicio(), jd.getFim(), TipoDisponibilidade.NORMAL);
    }

    /**
     * 🔹 Consulta tradicional (com acesso direto ao repositório).
     */
    public DisponibilidadeDia consultarDisponibilidade(Long profissionalId, LocalDate data) {
        JornadaProfissional jornada = obterJornadaCompleta(profissionalId);
        return consultarDisponibilidadeLocal(jornada, data);
    }

    /**
     * 🔹 Verifica se o profissional está disponível em um horário específico.
     */
    public boolean estaDisponivel(Long profissionalId, LocalDateTime inicio, LocalDateTime fim) {
        DisponibilidadeDia disponibilidade = consultarDisponibilidade(profissionalId, inicio.toLocalDate());
        if (disponibilidade.tipo() == TipoDisponibilidade.FOLGA) return false;

        return !inicio.toLocalTime().isBefore(disponibilidade.inicio())
                && !fim.toLocalTime().isAfter(disponibilidade.fim());
    }
}
