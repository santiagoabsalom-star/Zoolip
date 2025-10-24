package com.surrogate.Zoolip.controllers.Bussines;

import com.surrogate.Zoolip.models.DTO.PublicacionDTO;
import com.surrogate.Zoolip.models.bussiness.Publicacion.Publicacion;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.services.bussiness.PublicacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/publicacion")
public class PublicacionController {
    private final PublicacionService publicacionService;

    @PostMapping("/crear")
    public ResponseEntity<Response> crearPublicacion(@RequestBody Publicacion publicacion){
        log.info("Id de usuario: {} ", publicacion.getId_usuario().getId());
           Response response= publicacionService.crear(publicacion);
        return ResponseEntity.status(response.getHttpError()).body(response);
    }
    @PostMapping("/actualizar")
    public ResponseEntity<Response> actualizarPublicacion(@RequestBody Publicacion publicacion){
        Response response= publicacionService.actualizar(publicacion);
        return ResponseEntity.status(response.getHttpError()).body(response);
    }
    @DeleteMapping("/eliminar")
    public ResponseEntity<Response> eliminarPublicacion(@RequestBody Long id_publicacion){
        Response response= publicacionService.eliminar(id_publicacion);
        return ResponseEntity.status(response.getHttpError()).body(response);
    }

    @GetMapping("/obtenerTodas")
    public ResponseEntity<List<PublicacionDTO>> obtenerTodas(){
    List<PublicacionDTO> publicaciones= publicacionService.obtenerTodas();
    if(publicaciones==null || publicaciones.isEmpty()){
        return ResponseEntity.ofNullable(null);
    }
    return ResponseEntity.ok(publicaciones);
    }


    @GetMapping("/obtenerPorId")
    public ResponseEntity<PublicacionDTO> obtenerPorId(@RequestBody Long id_publicacion){
    PublicacionDTO publicacion = publicacionService.obtenerPorId(id_publicacion);
    if(publicacion==null){
        return ResponseEntity.status(404).body(null);
    }
    return ResponseEntity.ok(publicacion);
    }
}