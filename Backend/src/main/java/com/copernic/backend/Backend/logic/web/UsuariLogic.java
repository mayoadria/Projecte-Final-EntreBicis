package com.copernic.backend.Backend.logic.web;

import com.copernic.backend.Backend.Excepciones.ExcepcionEmailDuplicado;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servei de lògica de negoci per a la gestió d'usuaris.
 * Proporciona operacions per crear, consultar, modificar i eliminar usuaris,
 * així com funcions específiques com la gestió de perfils i comprovació de duplicats.
 */
@Service
public class UsuariLogic {

    private static final Logger log = LoggerFactory.getLogger(UsuariLogic.class);
    @Autowired
    private UserRepository userRepository;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Crea un nou usuari al sistema després de validar la unicitat del correu electrònic.
     * La contrasenya es desa encriptada.
     *
     * @param usuari Objecte {@link Usuari} a crear.
     * @return L'usuari creat.
     * @throws ExcepcionEmailDuplicado si el correu ja existeix.
     */
    public Usuari createUsuari(Usuari usuari) {
        if (existeEmail(usuari.getEmail())) {
            throw new ExcepcionEmailDuplicado("El correu electrònic ja està registrat.");
        } else {
            // Encriptar la contraseña antes de guardar el usuario
            usuari.setContra(passwordEncoder.encode(usuari.getContra()));
            return userRepository.save(usuari);
        }
    }

    /**
     * Obté la llista de tots els usuaris.
     *
     * @return Llista de {@link Usuari}.
     */
    public List<Usuari> getAllUsuaris() {
        return userRepository.findAll();
    }

    /**
     * Cerca un usuari pel seu email (opcional).
     *
     * @param email Correu electrònic a buscar.
     * @return {@link Optional} d'usuari.
     */
    public Optional<Usuari> getUsuariByEmail(String email) {
        return userRepository.findById(email);
    }

    /**
     * Cerca un usuari pel seu email.
     *
     * @param email Correu electrònic a buscar.
     * @return L'usuari trobat o {@code null} si no existeix.
     */
    public Usuari getUsuariByEmaiL(String email) {
        return userRepository.findByEmail(email);
    }


    /**
     * Actualitza un usuari existent sense modificar el seu email (identificador).
     *
     * @param usuariActualitzat Objecte {@link Usuari} amb les dades a actualitzar.
     */
    public void updateUsuari(Usuari usuariActualitzat) {
        // No se toca el campo "email" ya que es el identificador
        userRepository.save(usuariActualitzat);

    }
    /**
     * Elimina un usuari pel seu email.
     *
     * @param email Correu electrònic de l'usuari a eliminar.
     */
    public void deleteUsuari(String email) {
        userRepository.deleteById(email);
    }

    /**
     * Comprova si un usuari amb el correu electrònic donat existeix.
     *
     * @param email Correu electrònic a verificar.
     * @return {@code true} si existeix, {@code false} si no.
     */
    public boolean existeEmail(String email) {
        Usuari usu = userRepository.findById(email).orElse(null);
        return usu != null;
    }

    /**
     * Cerca un usuari pel seu email.
     *
     * @param email Correu electrònic a buscar.
     * @return L'usuari trobat o {@code null} si no existeix.
     */
    public Usuari findByEmail(String email) {
        return userRepository.findById(email).orElse(null);
    }

    /**
     * Actualitza el perfil d'un usuari (nom, cognom, telèfon, població, etc.).
     * També permet actualitzar la contrasenya si es proporciona un valor en text pla.
     *
     * @param dto Objecte {@link Usuari} amb les dades a actualitzar.
     * @return L'email de l'usuari actualitzat.
     * @throws EntityNotFoundException si l'usuari no existeix.
     */
    @Transactional
    public String savePerfil(Usuari dto) {

        Usuari db = userRepository.findById(dto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException());

        db.setNom(dto.getNom());
        db.setCognom(dto.getCognom());
        db.setTelefon(dto.getTelefon());
        db.setPoblacio(dto.getPoblacio());
        db.setReserva(dto.getReserva());
        db.setFoto(dto.getFoto());
        //db.setRuta(dto.getRuta());
        // …cualquier otro campo editable…

        String nuevaPwd = dto.getContra();

        if (nuevaPwd != null && !nuevaPwd.isBlank()) {

            boolean mismaClave = nuevaPwd.equals(db.getContra());

            if (!mismaClave) {
                boolean yaEsHash = nuevaPwd.startsWith("$2");

                if (yaEsHash) {
                    // Llegó un hash distinto → lo ignoramos (podrías lanzar excepción)
                    log.warn("Intento de actualizar la contraseña con un hash ya codificado; se ignora");
                } else {
                    // Llegó texto plano → codificamos
                    db.setContra(passwordEncoder.encode(nuevaPwd));
                }
            }
        }

        userRepository.save(db);
        return db.getEmail();
    }
}
