package com.pmh.backendhomemedcare.repository;

import com.pmh.backendhomemedcare.model.entity.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfissionalRepo extends JpaRepository<Profissional, Long> {
}
