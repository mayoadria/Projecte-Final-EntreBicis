package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.Excepciones.ExcepcionEmailDuplicado;
import com.copernic.backend.Backend.entity.PuntBescanvi;
import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.entity.enums.EstatUsuari;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Base64;

@Controller
@RequestMapping("/usuaris")
public class UsuarisWebController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarisWebController.class);

    @Autowired
    private UsuariLogic usuariLogic;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Listar usuarios, excluyendo administradores
    @GetMapping
    public String showUsuaris(Model model) {
        try {
            if (!model.containsAttribute("error")) {
                model.addAttribute("error", null);
            }

            List<Usuari> usuaris = usuariLogic.getAllUsuaris().stream()
                    .filter(u -> u.getRol() != null)
                    .collect(Collectors.toList());

            model.addAttribute("usuaris", usuaris);
            return "usuaris";
        } catch (Exception e) {
            logger.error("Error al mostrar els usuaris", e);
            return "error";
        }
    }


    // Mostrar la página para crear un usuario
    @GetMapping("/crear")
    public String showCrearUsuariForm(Model model) {
        try {
            // Se añade una nueva instancia para asegurar que el formulario se vincule a un objeto nuevo
            model.addAttribute("newUsuari", new Usuari());
            return "crearUsuari";
        } catch (Exception e) {
            logger.error("Error al accedir a la pantalla de crear usuaris", e);
            return "error";
        }
    }

    // Crear un usuario asignando el rol CICLISTA y guardando la foto en Base64 (si se selecciona)
    @PostMapping("/crearUsuari")
    public String crearUsuari(@ModelAttribute("newUsuari") Usuari usuari,
                              @RequestParam(value = "fileFoto", required = false) MultipartFile fileFoto,
                              Model model) {
        try {
            if (fileFoto != null && !fileFoto.isEmpty()) {
                String base64Foto = Base64.getEncoder().encodeToString(fileFoto.getBytes());
                usuari.setFoto(base64Foto);
            }
            usuari.setRol(Rol.CICLISTA);
            usuari.setEstat(EstatUsuari.ACTIU);
            usuariLogic.createUsuari(usuari);
            return "redirect:/usuaris";
        } catch (ExcepcionEmailDuplicado e) {
            logger.error("L'email ja esta en ús" + e);
            model.addAttribute("error", e.getMessage());
            // Devolver el objeto con los datos ingresados para que se repopule el formulario
            model.addAttribute("newUsuari", usuari);
            return "crearUsuari";
        } catch (IOException ex) {
            logger.error("Error al crear el usuaris", ex);
            model.addAttribute("error", "Error procesando la imagen.");
            model.addAttribute("newUsuari", usuari);
            return "crearUsuari";
        }
    }


    // Mostrar el formulario de edición para un usuario dado su email
    @GetMapping("/editar/{email}")
    public String editarUsuariForm(@PathVariable String email, Model model) {
        try {
            Optional<Usuari> usuariOpt = usuariLogic.getUsuariByEmail(email);
            if (usuariOpt.isPresent()) {
                Usuari usuari = usuariOpt.get();
                // Si el usuario tiene foto, se crea el data URL
                if (usuari.getFoto() != null && !usuari.getFoto().isEmpty()) {
                    String fotoDataUrl = "data:image/jpeg;base64," + usuari.getFoto();
                    model.addAttribute("fotoDataUrl", fotoDataUrl);
                }
                model.addAttribute("usuariForm", usuari);
                model.addAttribute("visualitzar", false);
                model.addAttribute("estats", EstatUsuari.values());
                model.addAttribute("rols", Rol.values());
                // En la vista de edición se debe incluir un campo oculto "originalEmail" para evitar modificar el identificador
                return "editarUsuari";
            } else {
                return "redirect:/usuaris";
            }
        } catch (Exception e) {
            logger.error("Error al accedir a la pantalla d'editar un usuari", e);
            return "error";
        }
    }

    // Procesar la edición y actualizar el usuario. Se utiliza el email original para evitar modificar el identificador.
    @PostMapping("/editarUsuari")
    public String editarUsuari(@ModelAttribute("usuariForm") Usuari usuariForm, @RequestParam(value = "fileFoto", required = false) MultipartFile fileFoto) throws IOException {
        try {
            Optional<Usuari> existingOpt = usuariLogic.getUsuariByEmail(usuariForm.getEmail());
            if (existingOpt.isPresent()) {
                Usuari existing = existingOpt.get();
                existing.setNom(usuariForm.getNom());
                existing.setCognom(usuariForm.getCognom());
                // Actualizar la contraseña solo si se proporciona un nuevo valor distinto
                if (usuariForm.getContra() != null && !usuariForm.getContra().isEmpty() &&
                        !passwordEncoder.matches(usuariForm.getContra(), existing.getContra())) {
                    existing.setContra(passwordEncoder.encode(usuariForm.getContra()));
                }
                existing.setTelefon(usuariForm.getTelefon());
                existing.setSaldo(usuariForm.getSaldo());
                existing.setPoblacio(usuariForm.getPoblacio());
                existing.setRol(usuariForm.getRol());
                existing.setEstat(usuariForm.getEstat());
                if (fileFoto != null && !fileFoto.isEmpty()) {
                    String base64Foto = Base64.getEncoder().encodeToString(fileFoto.getBytes());
                    existing.setFoto(base64Foto);
                }
                usuariLogic.updateUsuari(existing);
            }
            return "redirect:/usuaris";
        } catch (Exception e) {
            logger.error("Error al editar un usuari", e);
            return "error";
        }
    }

    // Eliminar un usuario dado su email
    @GetMapping("/delete/{email:.+}")
    public String deleteUsuari(@PathVariable String email, RedirectAttributes redirectAttributes) {
        try {
            Usuari usu = usuariLogic.findByEmail(email);
            if (usu.getReserva()) {
                redirectAttributes.addFlashAttribute("error", "No es pot eliminar un usuari amb una reserva activa o una ruta activa.");
            } else {
                usuariLogic.deleteUsuari(email);
            }

            return "redirect:/usuaris";
        } catch (Exception e) {
            logger.error("Error al eliminar un usuari", e);
            return "error";
        }
    }

    @GetMapping("/toggleEstat/{email}")
    public String toggleEstatUsuari(@PathVariable String email,
                                    RedirectAttributes redirectAttributes) {
        try {

            usuariLogic.getUsuariByEmail(email).ifPresent(usuari -> {

                boolean reservaActiva = Boolean.TRUE.equals(usuari.getReserva()); // evita NPE

                if (usuari.getEstat() == EstatUsuari.ACTIU) {

                    if (reservaActiva) {
                        redirectAttributes.addFlashAttribute(
                                "error",
                                "No es pot desactivar un usuari amb una reserva activa."
                        );
                        return;                         // aborta sin modificar el usuari
                    }
                    usuari.setEstat(EstatUsuari.INACTIU);

                } else {
                    usuari.setEstat(EstatUsuari.ACTIU);
                }

                usuariLogic.updateUsuari(usuari);      // solo se llama si hay cambio válido
            });

            return "redirect:/usuaris";
        } catch (Exception e) {
            logger.error("Error al canviar l'estat d'un usuari", e);
            return "error";
        }
    }


    @InitBinder("usuari")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("email");
    }

    @GetMapping("/visualitzar/{email:.+}")
    public String visualitzarUsuari(@PathVariable String email, Model model) {
        try {
            // 1. Buscar el usuario
            Optional<Usuari> usuariOpt = usuariLogic.getUsuariByEmail(email);
            if (usuariOpt.isEmpty()) {
                // Si no existe, volvemos a la lista y mostramos error
                model.addAttribute("error", "L'usuari amb email " + email + " no existeix.");
                return "redirect:/usuaris";
            }

            // 2. Añadir información al modelo
            Usuari usuari = usuariOpt.get();

            // Si tiene foto, construimos el data-URL para mostrarla directamente en <img>
            if (usuari.getFoto() != null && !usuari.getFoto().isEmpty()) {
                String fotoDataUrl = "data:image/jpeg;base64," + usuari.getFoto();
                model.addAttribute("fotoDataUrl", fotoDataUrl);
            }

            // Reutilizamos el mismo nombre de atributo que en la edición
            model.addAttribute("usuariForm", usuari);

            // Flag para que la vista sepa que es modo lectura
            model.addAttribute("visualitzar", true);
            model.addAttribute("estats", EstatUsuari.values());
            model.addAttribute("rols", Rol.values());

            // 3. Reutilizamos la vista de edición
            return "editarUsuari";
        } catch (Exception e) {
            logger.error("Error al veure els detalls d'un usuari", e);
            return "error";
        }
    }



}
