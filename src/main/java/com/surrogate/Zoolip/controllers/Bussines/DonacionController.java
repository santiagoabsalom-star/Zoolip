package com.surrogate.Zoolip.controllers.Bussines;

import com.surrogate.Zoolip.models.DTO.DonacionDTO;
import com.surrogate.Zoolip.models.bussiness.Donacion.Donacion;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.services.bussiness.DonacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/donacion")
public class DonacionController {
    private final DonacionService donacionService;

    @PostMapping("/crear")
    public ResponseEntity<Response> crearDonacion(@RequestBody Donacion donacion) {
        Response response = donacionService.crearDonacion(donacion);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @PostMapping("/actualizar")
    public ResponseEntity<Response> actualizarDonacion(@RequestBody Donacion donacion) {
        Response response = donacionService.actualizarDonacion(donacion);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<Response> eliminarDonacion(@RequestBody Donacion donacion) {
        Response response = donacionService.eliminarDonacion(donacion);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @GetMapping("/obtenerTodas")
    public ResponseEntity<List<DonacionDTO>> obtenerTodas() {
        List<DonacionDTO> donaciones = donacionService.obtenerDonaciones();
        if (donaciones == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(donaciones, HttpStatus.OK);
    }

    @GetMapping("obtenerPorId")
    public ResponseEntity<DonacionDTO> obtenerPorId(Long idDonacion) {
        DonacionDTO donaiconeDTO = donacionService.obtenerDonacionById(idDonacion);
        if (donaiconeDTO == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(donaiconeDTO, HttpStatus.OK);
    }
}
