package com.copernic.backend.Backend.repository;

import com.copernic.backend.Backend.entity.PuntBescanvi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BescanviRepository extends JpaRepository<PuntBescanvi, Long> {
}
