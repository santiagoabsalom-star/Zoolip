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
        if (response.getStatus().equals("success")) {
            return ResponseEntity.ok(new Response("success", "La mascota ha sido agregada"));

        } else if (response.getHttpError().equals(400)) {
            return ResponseEntity.badRequest().body(response);
        } else {
            return ResponseEntity.status(500).body(new Response("error", "Error del servidor"));
        }
    }

    @PostMapping("/actualizar")
    public ResponseEntity<Response> updateMascota(@RequestBody Mascota mascota) {
        Response response = mascotaService.actualizarMascota(mascota);
        if (response.getStatus().equals("success")) {
            return ResponseEntity.ok(new Response("success", "La mascota ha sido modificada"));

        } else if (response.getHttpError().equals(400)) {
            return ResponseEntity.badRequest().body(response);
        } else {
            return ResponseEntity.status(500).body(new Response("error", "Error del servidor"));
        }


    }

    @PostMapping("/eliminar")
    public ResponseEntity<Response> deleteMascota(@RequestBody Mascota mascota) {
        Response response = mascotaService.eliminarMascota(mascota.getId());
        if (response.getStatus().equals("success")) {
            return ResponseEntity.ok(new Response("success", "La mascota ha sido elimada"));
        } else if (response.getHttpError().equals(400)) {
            return ResponseEntity.badRequest().body(response);
        } else {
            return ResponseEntity.status(500).body(new Response("error", "Error del servidor"));
        }
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
