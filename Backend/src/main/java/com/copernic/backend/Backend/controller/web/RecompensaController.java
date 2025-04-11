package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.PuntBescanvi;
import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.logic.web.PuntBescanviLogic;
import com.copernic.backend.Backend.logic.web.RecompensaLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/recompensas")
public class RecompensaController {

    @Autowired
    private RecompensaLogic logic;
    @Autowired
    private PuntBescanviLogic puntBescanviLogic;

    @GetMapping("/llistar")
    public String LlistarRecompensas(
            @RequestParam(name = "nomRecompensa", required = false) String nomRecompensa,
            @RequestParam(name = "puntBescanvi", required = false) String puntBescanvi,
            @RequestParam(name = "nomUsuari", required = false) String nomUsuari,
            @RequestParam(name = "rangPunts", required = false) String rangPunts,
            @RequestParam(name = "estat", required = false) Estat estat,
            @RequestParam(name = "ordenarPor", required = false) String ordenarPor,
            @RequestParam(name = "orden", required = false) String orden,
            Model model) {

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

        if (rangPunts != null && !rangPunts.isEmpty()) {
            try {
                String[] rang = rangPunts.split("-");
                int min = Integer.parseInt(rang[0].trim());
                int max = Integer.parseInt(rang[1].trim());

                recompensa = recompensa.stream()
                        .filter(r -> r.getCost() >= min && r.getCost() <= max)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                System.out.println("Error en el formato del rango de puntos");
            }
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
    }



    @GetMapping("/crear")
    public String Registre(Model model) {
        model.addAttribute("recompensas", new Recompensas());
        model.addAttribute("estat", Estat.values());
        model.addAttribute("bescanvi", puntBescanviLogic.llistarBescanvi());
        return "crearRecompensas"; // Vista "Registre"
    }

    @PostMapping("/guardar")
    public String guardarRecompensa(@ModelAttribute("recompensas") Recompensas recompensa,
                                    @RequestParam("bescanvi") Long puntBescanviId, Model model,
                                    @RequestParam(value = "fileFoto", required = false) MultipartFile fileFoto
    ) {
        try {
            // Buscar el Punto de Bescanvi en la base de datos
            PuntBescanvi bescanvi = puntBescanviLogic.findByID(puntBescanviId);
            if (fileFoto != null && !fileFoto.isEmpty()) {
                String base64Foto = Base64.getEncoder().encodeToString(fileFoto.getBytes());
                recompensa.setFoto(base64Foto);
            }
            // Asignarlo a la recompensa
            recompensa.setPuntBescanviId(bescanvi);
            recompensa.setDataCreacio(LocalDate.now().toString());
            if (recompensa.getEstat() == Estat.ASSIGNADES) {
                recompensa.setDataAsignacio(LocalDate.now().toString());
            }else{
                recompensa.setDataAsignacio("");
            }

            // Guardar la recompensa
            logic.guardarRecompensa(recompensa);

            return "redirect:/recompensas/llistar";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteRecompensa(@PathVariable Long id) {
        logic.eliminarRecompensa(id);

        return "redirect:/recompensas/llistar";

    }

    @GetMapping("/edit/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
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
        model.addAttribute("visualizar", false); // Editar => visualizar desactivado
        model.addAttribute("error", false); // Editar => visualizar desactivado
        return "modificarRecompensa";
    }

    @PostMapping("/editar")
    public String guardarCambios(@ModelAttribute Recompensas recompensa,
                                 @RequestParam("puntBescanviId") Long puntBescanviId,
                                 Model model,
                                 @RequestParam(value = "fileFoto", required = false) MultipartFile fileFoto) throws IOException {

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
                    recompensaExiste.setDataAsignacio(LocalDate.now().toString()); // Asignar la fecha de asignación
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

                return "redirect:/recompensas/llistar";

            } else {
                model.addAttribute("error", "La recompensa está " + recompensaExiste.getEstat());
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
            model.addAttribute("visualizar", false);if (recompensaExiste.getFoto() != null && !recompensaExiste.getFoto().isEmpty()) {
                String fotoDataUrl = "data:image/jpeg;base64," + recompensaExiste.getFoto();
                model.addAttribute("fotoDataUrl", fotoDataUrl);
            }

            return "modificarRecompensa";

        }
    }

    @GetMapping("/visualizar/{id}")
    public String visualizarUsuario(@PathVariable Long id, Model model) {
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
    }



}
