package com.copernic.backend.Backend.repository;

import com.copernic.backend.Backend.entity.Recompensas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecompensasRepository extends JpaRepository<Recompensas, Long> {
    List<Recompensas> findByPuntBescanviId_Id(Long puntId);
    List<Recompensas> findByUsuariRecompensa_Email(String email);

}
