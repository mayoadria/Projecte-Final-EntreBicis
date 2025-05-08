    package com.copernic.backend.Backend.controller.android;

    import com.copernic.backend.Backend.controller.web.UsuariController;
    import com.copernic.backend.Backend.entity.Token;
    import com.copernic.backend.Backend.entity.Usuari;
    import com.copernic.backend.Backend.entity.enums.EstatUsuari;
    import com.copernic.backend.Backend.logic.web.SendEmailLogic;
    import com.copernic.backend.Backend.logic.web.TokenService;
    import com.copernic.backend.Backend.logic.web.UsuariLogic;
    import jakarta.annotation.PostConstruct;
    import jakarta.transaction.Transactional;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Optional;
    import java.util.UUID;

    @RestController
    @RequestMapping("/api/usuari")
    public class ApiUsuariController {

        private static final Logger log = LoggerFactory.getLogger(UsuariController.class);
        @Autowired
        private UsuariLogic logic;
        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private TokenService tokenLogic;

        @Autowired
        private SendEmailLogic sendEmailLogic;

        @PostConstruct
        private void init() {
            // Inicialización si es necesaria
        }


        @GetMapping("/validar/{email}/{contra}")
        public ResponseEntity<Usuari> validateUser(@PathVariable String email,
                                                   @PathVariable String contra) {

            log.debug("⏩  Petición de login → email='{}'", email);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-store");

            try {
                Optional<Usuari> opt = logic.getUsuariByEmail(email);
                if (opt.isEmpty()) {
                    log.warn("✖  Usuario no encontrado → {}", email);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                Usuari usuari = opt.get();
                log.debug("✔  Usuario encontrado. Estat='{}'", usuari.getEstat());

                // ¿Usuario inactivo?
                if (usuari.getEstat() == EstatUsuari.INACTIU) {
                    log.warn("✖  Usuario INACTIVO → {}", email);
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }

                // ¿Coincide la contraseña?
                boolean okPwd = passwordEncoder.matches(contra, usuari.getContra());
                if (!okPwd) {
                    log.warn("✖  Contraseña incorrecta → {}", email);
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }

                log.info("✅  Login correcto → {}", email);
                return new ResponseEntity<>(usuari, headers, HttpStatus.OK);

            } catch (Exception ex) {
                log.error("💥  Error inesperado validando usuario '{}'", email, ex);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }


        @GetMapping("/read/all")
        public ResponseEntity<List<Usuari>> findAll() {

            List<Usuari> llista;

            //el transporte HTTP
            ResponseEntity<List<Usuari>> response;

            //la cabecera del transporte
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-store"); //no usar caché

            try {

                llista = logic.getAllUsuaris();

                response = new ResponseEntity<>(llista, headers, HttpStatus.OK);

            } catch (Exception e) {

                response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return response;
        }

        @PutMapping("/update")
        public ResponseEntity<Void> updateUserById(@RequestBody Usuari client) {

            ResponseEntity<Void> resposta;

            try {
                if (client != null) {

                    if (logic.existeEmail(client.getEmail())) {

                        logic.savePerfil(client);
                        resposta = ResponseEntity.ok().build();
                    } else {
                        resposta = ResponseEntity.notFound().build();
                    }
                } else {
                    resposta = ResponseEntity.badRequest().build();
                }
            } catch (Exception e) {
                resposta = ResponseEntity.internalServerError().build();
            }
            return resposta;
        }

        @GetMapping("/getByEmail/{email}")
        public ResponseEntity<Usuari> getByEmail(@PathVariable String email) {

            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-store");

            try {
                Optional<Usuari> client = logic.getUsuariByEmail(email);

                return client
                        .map(usuari -> new ResponseEntity<>(usuari, headers, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        @PostMapping("/sendEmail/{email}")
        public ResponseEntity<Void> sendEmail(@PathVariable String email) {

            ResponseEntity<Void> resposta = null;

            Usuari client;

            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-store");

            try {
                if (!logic.existeEmail(email)) {
                    resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
                } else {
                    client = logic.getUsuariByEmaiL(email);

                    deleteToken(client);

                    String token = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

                    Token resetToken = new Token(token, client);

                    tokenLogic.saveToken(resetToken);

                    sendEmailLogic.sendEmail(email, token);

                    resposta = new ResponseEntity<>(HttpStatus.OK);
                }
            } catch (Exception e) {

                resposta = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return resposta;
        }

        @GetMapping("/validateToken/{token}/{email}")
        public ResponseEntity<Boolean> validateToken(@PathVariable String token, @PathVariable String email) {
            ResponseEntity<Boolean> resposta = null;

            Boolean isValid = false;

            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-store");

            try {
                Optional<Token> tokenBD = tokenLogic.getByToken(token);

                if (tokenBD.isPresent()) {
                    if (tokenBD.get().getUsuari().getEmail().equals(email)) {
                        if (!tokenBD.get().isExpired()) {
                            isValid = true;
                            resposta = new ResponseEntity<>(isValid, headers, HttpStatus.OK);
                        } else {
                            resposta = new ResponseEntity<>(isValid, headers, HttpStatus.UNAUTHORIZED);
                        }
                    } else {
                        resposta = new ResponseEntity<>(isValid, headers, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    resposta = new ResponseEntity<>(isValid, headers, HttpStatus.NOT_FOUND);
                }
            } catch (Exception e) {
                resposta = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return resposta;
        }

        @Transactional
        public void deleteToken(Usuari client) {
            Optional<Token> token = tokenLogic.getByClient(client);
            token.ifPresent(tokenLogic::deleteToken);
        }

    }
