package com.surrogate.Zoolip.controllers.Bussines;


import com.surrogate.Zoolip.models.DTO.InstitucionDTO;
import com.surrogate.Zoolip.models.DTO.InstitucionSolicitudDTO;
import com.surrogate.Zoolip.models.bussiness.Institucion.Institucion;
import com.surrogate.Zoolip.models.bussiness.Institucion.InstitucionSolicitud;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.services.bussiness.InstitucionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/institucion")
public class InstitucionController {
    private final InstitucionService institucionService;


    @PostMapping("/agregar")
    public ResponseEntity<Response> agregarInstitucion(@RequestBody Institucion institucion) {
        Response response = institucionService.crear(institucion);

        return ResponseEntity.status(response.getHttpCode()).body(response);
    }
    @PostMapping("/solicitudInstitucion")
    public ResponseEntity<Response> solicitud(@RequestBody InstitucionSolicitud institucionSolicitud) {
        Response response=institucionService.crearSolicitud(institucionSolicitud);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }


    @PostMapping("/actualizar")
    public ResponseEntity<Response> actualizarInstitucion(@RequestBody Institucion institucion) {
        Response response = institucionService.actualizar(institucion);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @PostMapping("/eliminar")
    public ResponseEntity<Response> eliminarInstitucion(@RequestBody long id_institucion) {
        Response response = institucionService.eliminar(id_institucion);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @GetMapping(value = "/obtenerTodas", produces = "application/json")
    public ResponseEntity<List<InstitucionDTO>> obtenerInstituciones() {
        try {
            return ResponseEntity.ok(institucionService.getInstituciones());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }
    @GetMapping("/obtenerSolicitudes")
    public ResponseEntity<List<InstitucionSolicitudDTO>> obtenerSolicitudes() {
        List<InstitucionSolicitudDTO> institucionSolicitudDTO= institucionService.findAllSolicitudes();
        if(institucionSolicitudDTO.isEmpty()){
            return ResponseEntity.ofNullable(null);

        }
        return ResponseEntity.ok(institucionSolicitudDTO);
    }

    @GetMapping("/obtenerPorId")
    public ResponseEntity<InstitucionDTO> obtenerInstitucionPorId(@RequestParam("id") long id) {
        try {

            return ResponseEntity.ok(institucionService.getInstitucion(id));

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
        @GetMapping("/obtenerPorIdUsuario")
        public ResponseEntity<InstitucionDTO> obtenerInstitucionPorIdUsuario (@RequestParam("id_usuario") long id_usuario){
            try {
                InstitucionDTO institucionDTO = institucionService.getInstitucionById_Usuario(id_usuario);
                if (institucionDTO==null) {
                    return ResponseEntity.status(404).body(null);
                }
                return ResponseEntity.ok(institucionDTO);
            } catch (Exception e) {

                return ResponseEntity.status(500).build();
            }
        }
        @PostMapping("/respuestaSolicitud")
    public ResponseEntity<Response> respuestaSolicitud(@RequestBody InstitucionSolicitud solicitud) {
        Response response = institucionService.responseSolicitud(solicitud);
        return ResponseEntity.status(response.getHttpCode()).body(response);
        }
    }

