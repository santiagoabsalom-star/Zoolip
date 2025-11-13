package com.surrogate.Zoolip.controllers.Bussines;

import com.surrogate.Zoolip.models.DTO.MascotaDTO;
import com.surrogate.Zoolip.models.DTO.SolicitudAdopcionDTO;
import com.surrogate.Zoolip.models.bussiness.Mascota.Mascota;
import com.surrogate.Zoolip.models.bussiness.Mascota.SolicitudAdopcion.SolicitudAdopcion;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.services.bussiness.MascotaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @PostMapping("/actualizar")
    public ResponseEntity<Response> updateMascota(@RequestBody Mascota mascota) {
        Response response = mascotaService.actualizarMascota(mascota);
        return ResponseEntity.status(response.getHttpCode()).body(response);


    }

    @PostMapping("/eliminar")
    public ResponseEntity<Response> deleteMascota(@RequestBody Mascota mascota) {
        Response response = mascotaService.eliminarMascota(mascota.getId());
        return ResponseEntity.status(response.getHttpCode()).body(response);
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
    @GetMapping("/misMascotas")
        public ResponseEntity<List<MascotaDTO>> misMascotas(){
        List<MascotaDTO> misMascotas= mascotaService.buscarMisMascotasDTO();
        if(misMascotas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        }
        return ResponseEntity.ok(misMascotas);
    }
    @PostMapping("/solicitudAdopcion")
    public ResponseEntity<Response> solicitudAdopcion(@RequestBody SolicitudAdopcion solicitudAdopcion) {
        Response response = mascotaService.solicitudAdopcion( solicitudAdopcion );
        return ResponseEntity.status(response.getHttpCode()).body(response);

    }
    @PostMapping("/completarAdopcion")
    public ResponseEntity<Response> completarAdopcion(@RequestBody SolicitudAdopcion solicitudAdopcion) {
        Response response = mascotaService.completarAdopcion( solicitudAdopcion );
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }
    @GetMapping("/getAllSolicitudes")
    public ResponseEntity<List<SolicitudAdopcionDTO>> getAllSolicitudes() {
        List<SolicitudAdopcionDTO> solicitudes = mascotaService.getAllSolicitudes();
        if(solicitudes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(solicitudes, HttpStatus.OK);

    }
    @GetMapping("/getSolicitudById")
    public ResponseEntity<SolicitudAdopcionDTO> getSolicitudById(@RequestParam Long id) {
        SolicitudAdopcionDTO solicitud= mascotaService.getSolicitudAdopcionById(id);
        if(solicitud == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }
        return new ResponseEntity<>(solicitud, HttpStatus.OK);
    }

}
