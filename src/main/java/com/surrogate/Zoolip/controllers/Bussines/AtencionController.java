package com.surrogate.Zoolip.controllers.Bussines;

import com.surrogate.Zoolip.models.DTO.AtiendeDTO;
import com.surrogate.Zoolip.models.bussiness.Atencion.Atiende;
import com.surrogate.Zoolip.models.bussiness.Atencion.AtiendeId;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.services.bussiness.AtencionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atencion")
@Slf4j
@RequiredArgsConstructor
public class AtencionController {
    private final AtencionService atencionService;

    @PostMapping("/empezar")
    public ResponseEntity<Response> empezar(Atiende atiende){
        Response response = atencionService.empezar(atiende);
        return ResponseEntity.status(response.getHttpCode()).body(response);

    }
    @PostMapping("/actualizar")
    public ResponseEntity<Response> actualizar(Atiende atiende){
        Response response = atencionService.actualizar(atiende);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }
    @DeleteMapping("/eliminar")
    public ResponseEntity<Response> eliminar(Atiende atiende){
        Response response = atencionService.eliminar(atiende);
        return ResponseEntity.status(response.getHttpCode()).body(response);

    }
    @PostMapping("/completar")
    public ResponseEntity<Response> completar(Atiende atiende){
        Response response = atencionService.completar(atiende);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }
    @GetMapping("/obtenerTodas")
    public ResponseEntity<List<AtiendeDTO>> obtenerTodas(){
        List<AtiendeDTO> atenciones = atencionService.obtenerTodas();
        if(atenciones==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(atenciones, HttpStatus.OK);
    }
    @GetMapping("/obtenerPorId")
    public ResponseEntity<AtiendeDTO> obtenerPorId(AtiendeId atiendeId)
    {
        AtiendeDTO atiendeDTO = atencionService.obtenerPorId(atiendeId);
        if(atiendeDTO==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(atiendeDTO, HttpStatus.OK);
    }

}
