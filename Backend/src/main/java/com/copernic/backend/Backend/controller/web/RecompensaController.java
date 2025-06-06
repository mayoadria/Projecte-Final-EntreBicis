package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.PuntBescanvi;
import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.logic.web.PuntBescanviLogic;
import com.copernic.backend.Backend.logic.web.RecompensaLogic;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador web per gestionar les recompenses.
 * <p>
 * Inclou operacions per llistar, crear, editar, eliminar i visualitzar recompenses,
 * així com filtres i consultes per punts de bescanvi i usuaris.
 * </p>
 */
@Controller
@RequestMapping("/recompensas")
public class RecompensaController {

    private static final Logger logger = LoggerFactory.getLogger(RecompensaController.class);
    @Autowired
    private RecompensaLogic logic;
    @Autowired
    private PuntBescanviLogic puntBescanviLogic;
    @Autowired
    private RecompensaLogic recompensaLogic;
    @Autowired
    private UsuariLogic usuariLogic;

    /**
     * Llista les recompenses amb opcions de filtratge i ordenació.
     *
     * @param nomRecompensa Nom parcial de la recompensa per filtrar.
     * @param puntBescanvi Nom del punt de bescanvi per filtrar.
     * @param nomUsuari Nom de l'usuari assignat a la recompensa per filtrar.
     * @param rangPunts Rang de punts en format "min-max" per filtrar.
     * @param estat Estat de la recompensa per filtrar.
     * @param ordenarPor Camp pel qual ordenar (cost, dataAsignacio, dataCreacio).
     * @param orden Ordre de llistat (asc o desc).
     * @param model Model per passar dades a la vista.
     * @return Vista de llistat de recompenses o pàgina d'error.
     */
    @GetMapping("/llistar")
    public String LlistarRecompensas(
            @RequestParam(name = "nomRecompensa", required = false) String nomRecompensa,
            @RequestParam(name = "puntBescanvi", required = false) String puntBescanvi,
            @RequestParam(name = "nomUsuari", required = false) String nomUsuari,
            @RequestParam(name = "minPunts", required = false) Integer minPunts,
            @RequestParam(name = "maxPunts", required = false) Integer maxPunts,
            @RequestParam(name = "estat", required = false) Estat estat,
            @RequestParam(name = "ordenarPor", required = false) String ordenarPor,
            @RequestParam(name = "orden", required = false) String orden,
            Model model) {

        try {
            List<Recompensas> recompensa = logic.llistarRecompensas();

            if (nomRecompensa != null && !nomRecompensa.isEmpty()) {
                recompensa = recompensa.stream()
                        .filter(r -> r.getDescripcio().toLowerCase().contains(nomRecompensa.toLowerCase()))
                        .collect(Collectors.toList());
            }

            if (puntBescanvi != null && !puntBescanvi.isEmpty()) {
                recompensa = recompensa.stream()
                        .filter(r -> r.getPuntBescanviId() != null &&
                                r.getPuntBescanviId().getNom().toLowerCase().contains(puntBescanvi.toLowerCase()))
                        .collect(Collectors.toList());
            }

            if (nomUsuari != null && !nomUsuari.isEmpty()) {
                recompensa = recompensa.stream()
                        .filter(r -> r.getUsuariRecompensa() != null &&
                                r.getUsuariRecompensa().getNom().toLowerCase().contains(nomUsuari.toLowerCase()))
                        .collect(Collectors.toList());
            }

            if (minPunts != null || maxPunts != null) {
                int min = (minPunts != null) ? minPunts : Integer.MIN_VALUE;
                int max = (maxPunts != null) ? maxPunts : Integer.MAX_VALUE;

                recompensa = recompensa.stream()
                        .filter(r -> r.getCost() >= min && r.getCost() <= max)
                        .collect(Collectors.toList());
            }

            if (estat != null) {
                recompensa = recompensa.stream()
                        .filter(r -> r.getEstat().name().equalsIgnoreCase(estat.name()))
                        .collect(Collectors.toList());
            }

            // Lógica de ordenación
            if (ordenarPor != null) {
                if (ordenarPor.equals("cost")) {
                    if ("desc".equals(orden)) {
                        recompensa = recompensa.stream()
                                .sorted((r1, r2) -> Integer.compare(r2.getCost(), r1.getCost()))  // Orden descendente por costo
                                .collect(Collectors.toList());
                    } else {
                        recompensa = recompensa.stream()
                                .sorted((r1, r2) -> Integer.compare(r1.getCost(), r2.getCost()))  // Orden ascendente por costo
                                .collect(Collectors.toList());
                    }
                } else if (ordenarPor.equals("dataAsignacio")) {
                    if ("desc".equals(orden)) {
                        recompensa = recompensa.stream()
                                .sorted((r1, r2) -> r2.getDataAsignacio().compareTo(r1.getDataAsignacio()))  // Orden descendente por fecha
                                .collect(Collectors.toList());
                    } else {
                        recompensa = recompensa.stream()
                                .sorted((r1, r2) -> r1.getDataAsignacio().compareTo(r2.getDataAsignacio()))  // Orden ascendente por fecha
                                .collect(Collectors.toList());
                    }
                } else if (ordenarPor.equals("dataCreacio")) {
                    if ("desc".equals(orden)) {
                        recompensa = recompensa.stream()
                                .sorted((r1, r2) -> r2.getDataCreacio().compareTo(r1.getDataCreacio()))  // Orden descendente por fecha
                                .collect(Collectors.toList());
                    } else {
                        recompensa = recompensa.stream()
                                .sorted((r1, r2) -> r1.getDataCreacio().compareTo(r2.getDataCreacio()))  // Orden ascendente por fecha
                                .collect(Collectors.toList());
                    }
                }
            }

            model.addAttribute("recompensas", recompensa);
            model.addAttribute("estats", Estat.values());
            return "llistarRecompensas";
        } catch (Exception e) {
            logger.error("Error al listar recompensas", e);
            model.addAttribute("errorMessage", "No s'ha pogut carregar la llista de recompenses.");
            return "error";
        }
    }

    /**
     * Mostra el formulari per crear una nova recompensa.
     *
     * @param model Model per passar dades a la vista.
     * @return Vista del formulari de creació o pàgina d'error.
     */
    @GetMapping("/crear")
    public String Registre(Model model) {
        try {
            model.addAttribute("recompensas", new Recompensas());
            model.addAttribute("estat", Estat.values());
            model.addAttribute("bescanvi", puntBescanviLogic.llistarBescanvi());
            return "crearRecompensas"; // Vista "Registre"
        } catch (Exception e) {
            logger.error("Error al mostrar la pagina de crear una recompensa", e);
            model.addAttribute("errorMessage", "No s'ha pogut carregar la llista de recompenses.");
            return "error";
        }
    }
    /**
     * Guarda una nova recompensa a la base de dades.
     *
     * @param recompensa Objecte Recompensas amb les dades del formulari.
     * @param puntBescanviId ID del punt de bescanvi associat.
     * @param model Model per passar dades a la vista.
     * @param fileFoto Arxiu d'imatge opcional per la recompensa.
     * @return Redirecció al llistat de recompenses o pàgina d'error.
     */
    @PostMapping("/guardar")
    public String guardarRecompensa(@ModelAttribute("recompensas") Recompensas recompensa,
                                    @RequestParam("bescanvi") Long puntBescanviId,
                                    Model model,
                                    @RequestParam(value = "fileFoto", required = false) MultipartFile fileFoto,
                                    RedirectAttributes redirectAttributes) {
        try {
            PuntBescanvi bescanvi = puntBescanviLogic.findByID(puntBescanviId);
            if (fileFoto != null && !fileFoto.isEmpty()) {
                String base64Foto = Base64.getEncoder().encodeToString(fileFoto.getBytes());
                recompensa.setFoto(base64Foto);
            }
            recompensa.setPuntBescanviId(bescanvi);
            recompensa.setDataCreacio(LocalDateTime.now());

            if (recompensa.getEstat() == Estat.ASSIGNADES) {
                recompensa.setDataAsignacio(LocalDateTime.now());
            }

            logic.guardarRecompensa(recompensa);

            // ✅ Afegeix missatge flash
            redirectAttributes.addFlashAttribute("successMessage", "Recompensa creada correctament.");
            return "redirect:/recompensas/llistar";

        } catch (Exception e) {
            logger.error("Error al crear una recompensa", e);
            return "error";
        }
    }

    /**
     * Elimina una recompensa identificada pel seu ID.
     *
     * @param id ID de la recompensa a eliminar.
     * @return Redirecció al llistat de recompenses o pàgina d'error.
     */
    @GetMapping("/delete/{id}")
    public String deleteRecompensa(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            logic.eliminarRecompensa(id);
            redirectAttributes.addFlashAttribute("successMessage", "Recompensa eliminada correctament.");
        } catch (Exception e) {
            logger.error("Error al eliminar una recompensa", e);
            redirectAttributes.addFlashAttribute("error", "No s'ha pogut eliminar la recompensa. Pot estar assignada o vinculada.");
        }
        return "redirect:/recompensas/llistar";
    }

    /**
     * Mostra el formulari per editar una recompensa.
     *
     * @param id ID de la recompensa a editar.
     * @param model Model per passar dades a la vista.
     * @return Vista d'edició de recompensa o pàgina d'error.
     */
    @GetMapping("/edit/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        try{
        Recompensas recompensas = logic.findById(id);
        PuntBescanvi bes = puntBescanviLogic.findByID(recompensas.getPuntBescanviId().getId());
        if (recompensas == null) {
            return "redirect:/recompensas/llistar";
        }
        if (recompensas.getFoto() != null && !recompensas.getFoto().isEmpty()) {
            String fotoDataUrl = "data:image/jpeg;base64," + recompensas.getFoto();
            model.addAttribute("fotoDataUrl", fotoDataUrl);
        }
        model.addAttribute("recompensas", recompensas);
        model.addAttribute("estat", Estat.values());
        model.addAttribute("puntBescanviId", puntBescanviLogic.llistarBescanvi());
        model.addAttribute("puntBescanvi", bes);
        model.addAttribute("visualizar", false); // Editar => visualizar desactivado
        model.addAttribute("error", false); // Editar => visualizar desactivado
        return "modificarRecompensa";
        } catch (Exception e) {
            logger.error("Error al accedir a la pantalla per editar una recompensa", e);
            return "error";
        }
    }

    /**
     * Guarda els canvis realitzats en una recompensa existent.
     *
     * @param recompensa Objecte Recompensas amb les dades modificades.
     * @param puntBescanviId ID del punt de bescanvi associat.
     * @param model Model per passar dades a la vista.
     * @param fileFoto Nova imatge opcional per la recompensa.
     * @return Redirecció al llistat de recompenses o pàgina d'error.
     * @throws IOException En cas d'error al processar la imatge.
     */
    @PostMapping("/editar")
    public String guardarCambios(@ModelAttribute Recompensas recompensa,
                                 @RequestParam("puntBescanviId") Long puntBescanviId,
                                 Model model,
                                 @RequestParam(value = "fileFoto", required = false) MultipartFile fileFoto, RedirectAttributes redirectAttributes) throws IOException {

        try{
        // Lógica de actualización de recompensa
        Recompensas recompensaExiste = logic.findById(recompensa.getId());

        // Buscar el Punto de Bescanvi
        PuntBescanvi bescanvi = puntBescanviLogic.findByID(puntBescanviId);


        // Comprobar que la recompensa existe
        if (recompensaExiste != null) {
            if (recompensaExiste.getEstat() == Estat.DISPONIBLES) {
                // Actualizar los valores
                recompensaExiste.setDescripcio(recompensa.getDescripcio());
                recompensaExiste.setCost(recompensa.getCost());
                recompensaExiste.setObservacions(recompensa.getObservacions());
                if (recompensa.getEstat().equals(Estat.ASSIGNADES) && !recompensaExiste.getEstat().equals(Estat.ASSIGNADES)) {
                    recompensaExiste.setDataAsignacio(LocalDateTime.now()); // Asignar la fecha de asignación
                    System.out.println("✅ Fecha de asignación establecida: " + recompensaExiste.getDataAsignacio());
                }
                recompensaExiste.setEstat(recompensa.getEstat());
                recompensaExiste.setPuntBescanviId(bescanvi);

                // Si hay foto nueva, guardarla
                if (fileFoto != null && !fileFoto.isEmpty()) {
                    String base64Foto = Base64.getEncoder().encodeToString(fileFoto.getBytes());
                    recompensaExiste.setFoto(base64Foto);
                }

                // Verificar si el estado ha cambiado a ASSIGNADES


                // Guardar la recompensa modificada
                logic.modificarRecompensa(recompensaExiste);
                redirectAttributes.addFlashAttribute("successMessage", "Recompensa modificada correctament.");
                return "redirect:/recompensas/llistar";

            } else {
                model.addAttribute("error", "La recompensa no es pot modificar perquè està en estat " + recompensaExiste.getEstat().toString().toLowerCase());
                model.addAttribute("recompensas", recompensaExiste);
                model.addAttribute("estat", Estat.values());
                model.addAttribute("puntBescanviId", puntBescanviLogic.llistarBescanvi());
                model.addAttribute("visualizar", false);
                if (recompensaExiste.getFoto() != null && !recompensaExiste.getFoto().isEmpty()) {
                    String fotoDataUrl = "data:image/jpeg;base64," + recompensaExiste.getFoto();
                    model.addAttribute("fotoDataUrl", fotoDataUrl);
                }
                return "modificarRecompensa";  // Mostrar error en la vista
            }
        } else {
            model.addAttribute("error", "La recompensa no existe o no es válida.");
            model.addAttribute("recompensas", recompensa);
            model.addAttribute("estat", Estat.values());
            model.addAttribute("puntBescanviId", puntBescanviLogic.llistarBescanvi());
            model.addAttribute("visualizar", false);
            if (recompensaExiste.getFoto() != null && !recompensaExiste.getFoto().isEmpty()) {
                String fotoDataUrl = "data:image/jpeg;base64," + recompensaExiste.getFoto();
                model.addAttribute("fotoDataUrl", fotoDataUrl);
            }

            return "modificarRecompensa";

        }
        } catch (Exception e) {
            logger.error("Error al editar una recompensa", e);
            return "error";
        }
    }

    /**
     * Mostra els detalls d'una recompensa en mode visualització.
     *
     * @param id ID de la recompensa a visualitzar.
     * @param model Model per passar dades a la vista.
     * @return Vista de visualització de recompensa o pàgina d'error.
     */
    @GetMapping("/visualizar/{id}")
    public String visualizarUsuario(@PathVariable Long id, Model model) {
        try{


        Recompensas recompensas = logic.findById(id);
        if (recompensas == null) {
            return "redirect:/recompensas/llistar";
        }
        if (recompensas.getFoto() != null && !recompensas.getFoto().isEmpty()) {
            String fotoDataUrl = "data:image/jpeg;base64," + recompensas.getFoto();
            model.addAttribute("fotoDataUrl", fotoDataUrl);
        }
        model.addAttribute("recompensas", recompensas);
        model.addAttribute("estat", Estat.values());
        model.addAttribute("puntBescanviId", puntBescanviLogic.llistarBescanvi());
        model.addAttribute("visualizar", true); // Visualizar => campos deshabilitados

        return "modificarRecompensa";
        } catch (Exception e) {
            logger.error("Error al visualitzar els detalls d'una recompensa", e);
            return "error";
        }
    }

    /**
     * Llista les recompenses associades a un punt de bescanvi.
     *
     * @param puntId ID del punt de bescanvi.
     * @param model Model per passar dades a la vista.
     * @param redirectAttributes Missatges d'error en cas d'errada.
     * @return Vista de recompenses filtrades o redirecció.
     */
    @GetMapping("/punt/{puntId}")
    public String llistarPerPunt(
            @PathVariable("puntId") Long puntId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try{
        PuntBescanvi punt = puntBescanviLogic.findByID(puntId);
        if (punt == null) {
            redirectAttributes.addFlashAttribute("error", "El punt de bescanvi no existe.");
            return "redirect:/bescanvi/llistar";
        }

        // Recupera sólo las recompensas de este punto
        List<Recompensas> recompensas = logic.llistarPerPunt(puntId);

        model.addAttribute("recompensas", recompensas);
        model.addAttribute("estats", Estat.values());
        return "llistarRecompensas";
        } catch (Exception e) {
            logger.error("Error al llistar les recompenses del punt de bescanvi seleccionat", e);
            return "error";
        }
    }

    /**
     * Llista les recompenses associades a un usuari (pel seu email).
     *
     * @param recId Email de l'usuari associat.
     * @param model Model per passar dades a la vista.
     * @param redirectAttributes Missatges d'error en cas d'errada.
     * @return Vista de recompenses filtrades o redirecció.
     */
    @GetMapping("/recompensa/{recId}")
    public String llistarPerRecompensaUsuari(
            @PathVariable("recId") String puntId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try{
            Usuari rec = usuariLogic.findByEmail(puntId);
            if (rec == null) {
                redirectAttributes.addFlashAttribute("error", "El punt de bescanvi no existe.");
                return "redirect:/bescanvi/llistar";
            }

            // Recupera sólo las recompensas de este punto
            List<Recompensas> recompensas = logic.llistarPerEmailUsuari(puntId);

            model.addAttribute("recompensas", recompensas);
            model.addAttribute("estats", Estat.values());
            return "llistarRecompensas";
        } catch (Exception e) {
            logger.error("Error al llistar les recompenses del punt de bescanvi seleccionat", e);
            return "error";
        }
    }

}
