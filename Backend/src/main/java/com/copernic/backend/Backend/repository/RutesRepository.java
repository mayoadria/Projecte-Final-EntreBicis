package com.copernic.backend.Backend.repository;

import com.copernic.backend.Backend.entity.Rutes;
import com.copernic.backend.Backend.entity.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RutesRepository extends JpaRepository<Rutes, Long> {
    Optional<Rutes> findTopByUsuariOrderByIdDesc(Usuari usuari);
}
