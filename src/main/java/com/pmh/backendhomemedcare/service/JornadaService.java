package com.pmh.backendhomemedcare.service;

import com.pmh.backendhomemedcare.model.dto.out.DisponibilidadeDia;
import com.pmh.backendhomemedcare.model.entity.*;
import com.pmh.backendhomemedcare.model.enums.TipoDisponibilidade;
import com.pmh.backendhomemedcare.repository.JornadaProfissionalRepo;
import com.pmh.backendhomemedcare.repository.ProfissionalRepo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Optional;

@Service
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

        if (dia.isEmpty() || !dia.get().isTrabalha()) {
            return new DisponibilidadeDia(data, null, null, TipoDisponibilidade.FOLGA);
        }

        JornadaDia jd = dia.get();
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
