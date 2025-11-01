package com.pmh.backendhomemedcare.repository;

import com.pmh.backendhomemedcare.model.dto.out.OutAtendimentoDiaDto;
import com.pmh.backendhomemedcare.model.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    @Query("""
    SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END
    FROM Agendamento a
    WHERE a.profissional.id = :profissionalId
      AND a.cancelado = FALSE
      AND a.concluido = FALSE
      AND a.inicio < :fim
      AND a.fim > :inicio
""")
    boolean existsConflitoCriacao(
            @Param("profissionalId") Long profissionalId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    // Conflito para REAGENDAMENTO (ignora o próprio agendamento)
    @Query("""
    SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END
    FROM Agendamento a
    WHERE a.profissional.id = :profissionalId
      AND a.cancelado = FALSE
      AND a.concluido = FALSE
      AND a.id <> :agendamentoId
      AND a.inicio < :fim
      AND a.fim > :inicio
""")
    boolean existsConflitoReagendamento(
            @Param("profissionalId") Long profissionalId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("agendamentoId") Long agendamentoId
    );

    @Query("""
    SELECT a FROM Agendamento a
    WHERE a.profissional.id = :profissionalId
      AND a.cancelado = FALSE
      AND a.concluido = FALSE
      AND a.inicio BETWEEN :inicio AND :fim
""")
    List<Agendamento> findAtivosByProfissionalIdAndInicioBetween(
            @Param("profissionalId") Long profissionalId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    @Query("""
    select new com.pmh.backendhomemedcare.model.dto.out.OutAtendimentoDiaDto(
        a.id,
        a.paciente.nome,
        a.profissional.nome,
        a.inicio,
        a.fim
    )
    from Agendamento a
    where a.cancelado = false
      and a.profissional is not null
      and a.inicio between :inicio and :fim
    order by a.inicio asc
""")
    List<OutAtendimentoDiaDto> listarNaoCanceladosNoPeriodo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

}
