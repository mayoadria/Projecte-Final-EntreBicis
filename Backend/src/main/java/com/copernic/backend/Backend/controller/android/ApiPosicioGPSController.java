package com.copernic.backend.Backend.controller.android;

import com.copernic.backend.Backend.entity.Posicio_Gps;
import com.copernic.backend.Backend.entity.Rutes;
import com.copernic.backend.Backend.repository.GpsRepository;
import com.copernic.backend.Backend.repository.RutesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controlador REST para recibir y guardar puntos GPS asociados a una ruta
@RestController
@RequestMapping("/api/posicions")
public class ApiPosicioGPSController {
    private final GpsRepository gpsRepository;
    private final RutesRepository rutesRepository; // Necesitas implementarlo

    @Autowired
    public ApiPosicioGPSController(GpsRepository gpsRepository, RutesRepository rutesRepository) {
        this.gpsRepository = gpsRepository;
        this.rutesRepository = rutesRepository;
    }

    // Endpoint para guardar un punto GPS (POST /api/posicions/{rutaId})
    @PostMapping("/{rutaId}")
    public ResponseEntity<Posicio_Gps> savePosicio(@PathVariable Long rutaId, @RequestBody PosicioDto posicioDto) {

        // Buscar la ruta asociada al id
        Rutes ruta = rutesRepository.findById(rutaId).orElse(null);
        if (ruta == null) {
            return ResponseEntity.notFound().build();
        }

        // Construir la entidad Posicio_Gps con los datos recibidos y asociarla a la ruta
        Posicio_Gps posicio = Posicio_Gps.builder().latitud(posicioDto.getLatitud()).longitud(posicioDto.getLongitud()).temps(posicioDto.getTemps()).rutes(ruta).build();

        // Guardar el punto en la BD
        Posicio_Gps savedPosicio = gpsRepository.save(posicio);
        return ResponseEntity.ok(savedPosicio);
    }

    // DTO interno (puedes moverlo a su propio archivo si lo prefieres)
    public static class PosicioDto {
        private Double latitud;
        private Double longitud;
        private int temps;

        public Double getLatitud() {
            return latitud;
        }

        public void setLatitud(Double latitud) {
            this.latitud = latitud;
        }

        public Double getLongitud() {
            return longitud;
        }

        public void setLongitud(Double longitud) {
            this.longitud = longitud;
        }

        public int getTemps() {
            return temps;
        }

        public void setTemps(int temps) {
            this.temps = temps;
        }
    }
}
