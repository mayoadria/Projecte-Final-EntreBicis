package com.copernic.backend.Backend.repository;

import com.copernic.backend.Backend.entity.Reserva;
import com.copernic.backend.Backend.entity.Rutes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}
