package com.copernic.backend.Backend.repository;

import com.copernic.backend.Backend.entity.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Usuari, String> {
    boolean existsByEmail(String email);
}
