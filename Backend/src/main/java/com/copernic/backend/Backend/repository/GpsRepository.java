package com.copernic.backend.Backend.repository;

import com.copernic.backend.Backend.entity.Posicio_Gps;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GpsRepository extends JpaRepository<Posicio_Gps, Long> {
}
