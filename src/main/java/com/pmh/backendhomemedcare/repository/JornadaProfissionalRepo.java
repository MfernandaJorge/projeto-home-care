package com.pmh.backendhomemedcare.repository;

import com.pmh.backendhomemedcare.model.entity.JornadaProfissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JornadaProfissionalRepo extends JpaRepository<JornadaProfissional, Long> {

    Optional<JornadaProfissional> findByProfissionalId(Long profissionalId);

}
