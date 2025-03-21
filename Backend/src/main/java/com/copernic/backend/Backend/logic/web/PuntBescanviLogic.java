package com.copernic.backend.Backend.logic.web;

import com.copernic.backend.Backend.entity.PuntBescanvi;
import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.repository.BescanviRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PuntBescanviLogic {

    @Autowired
    BescanviRepository bescanviRepository;

    public List<PuntBescanvi> llistarBescanvi() {
        return bescanviRepository.findAll();
    }

    public PuntBescanvi findByID(Long id) {
        return bescanviRepository.findById(id).get();
    }
}
