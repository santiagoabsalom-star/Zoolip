package com.surrogate.Zoolip.controllers;


import com.surrogate.Zoolip.models.DTO.InstitucionDTO;
import com.surrogate.Zoolip.models.bussiness.Institucion.Institucion;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.services.bussiness.InstitucionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/institucion")
public class InstitucionController {
    private final InstitucionService institucionService;


    @PostMapping("/agregar")
    public ResponseEntity<Response> agregarInstitucion(@RequestBody Institucion institucion) {
        try {

            log.info(institucion.getNombre());

            Response response = institucionService.crear(institucion);
            if (response.getStatus().equals("success")) {
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body(response);

        }catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(e.getMessage()));

        }
    }
    @PostMapping("/actualizar")
    public ResponseEntity<Response> actualizarInstitucion(@RequestBody Institucion institucion) {
        try{
            Response response = institucionService.actualizar(institucion);
            if (response.getStatus().equals("success")) {
                return ResponseEntity.ok(response);

            }
            return ResponseEntity.badRequest().body(response);

        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(e.getMessage()));
        }
    }
    @PostMapping("/eliminar")
    public ResponseEntity<Response> eliminarInstitucion(@RequestBody long id_institucion) {
        try{
            Response response = institucionService.eliminar(id_institucion);
            if (response.getStatus().equals("success")) {
                return ResponseEntity.ok(response);

            }
            return ResponseEntity.badRequest().body(response);

        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(e.getMessage()));
        }
    }
    @GetMapping(value="/obtenerTodas" ,  produces = "application/json")
    public ResponseEntity<List<InstitucionDTO>> obtenerInstituciones(){
        try{
            return ResponseEntity.ok(institucionService.getInstituciones());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }
    @GetMapping("/obtenerPorId")
    public ResponseEntity<?> obtenerInstitucionPorId(@RequestParam("id") long id){
        try {
            return ResponseEntity.ok(institucionService.getInstitucion(id));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new Response(e.getMessage()));
        }

    }
}
