package com.surrogate.Zoolip.controllers.Bussines;

import com.surrogate.Zoolip.models.DTO.MascotaDTO;
import com.surrogate.Zoolip.models.bussiness.Mascota.Mascota;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.services.bussiness.MascotaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/mascotas")

public class MascotaController {
    private final MascotaService mascotaService;

    @PostMapping("/aniadir")
    public ResponseEntity<Response> createMascota(@RequestBody Mascota mascota) {
        Response response = mascotaService.agregarMascota(mascota);
      return ResponseEntity.status(response.getHttpError()).body(response);
    }

    @PostMapping("/actualizar")
    public ResponseEntity<Response> updateMascota(@RequestBody Mascota mascota) {
        Response response = mascotaService.actualizarMascota(mascota);
        return ResponseEntity.status(response.getHttpError()).body(response);


    }

    @PostMapping("/eliminar")
    public ResponseEntity<Response> deleteMascota(@RequestBody Mascota mascota) {
        Response response = mascotaService.eliminarMascota(mascota.getId());
        return ResponseEntity.status(response.getHttpError()).body(response);
    }

    @GetMapping("/obtenerTodas")
    public ResponseEntity<List<MascotaDTO>> getMascotas() {

        try {
            return ResponseEntity.ok(mascotaService.buscarMascotasDTO());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/obtenerPorId")
    public ResponseEntity<MascotaDTO> getMascotaById(@RequestParam Long id) {
        try {

            return ResponseEntity.ok(mascotaService.buscarById(id));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();

        }
    }

}
