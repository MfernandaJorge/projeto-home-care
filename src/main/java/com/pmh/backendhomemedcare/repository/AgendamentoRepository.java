package com.pmh.backendhomemedcare.repository;

import com.pmh.backendhomemedcare.model.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByProfissionalIdAndInicioBetween(Long profissionalId, LocalDateTime inicio, LocalDateTime fim);

    @Query("""
    SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END
    FROM Agendamento a
    WHERE a.profissional.id = :profissionalId
      AND a.inicio < :fim
      AND a.fim > :inicio
""")
    boolean existsByProfissionalAndIntervalo(
            @Param("profissionalId") Long profissionalId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

}

