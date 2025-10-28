package com.surrogate.Zoolip.controllers.Bussines;

import com.surrogate.Zoolip.models.DTO.ComentarioDTO;
import com.surrogate.Zoolip.models.bussiness.Publicacion.Comentario;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.services.bussiness.ComentarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/comentario")
public class ComentarioController {
    private final ComentarioService comentarioService;
        @PostMapping("/crear")
    public ResponseEntity<Response> crear(@RequestBody Comentario comentario){

            Response response= comentarioService.comentar(comentario);
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        @PostMapping("/actualizar")
    public ResponseEntity<Response> actualizar(@RequestBody Comentario comentario){
            Response response= comentarioService.actualizar(comentario);
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        @DeleteMapping("/eliminar")
    public ResponseEntity<Response> delete(@RequestBody Comentario comentario){
            Response response = comentarioService.eliminar(comentario);
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        @GetMapping("/obtenerTodos")
    public ResponseEntity<List<ComentarioDTO>> obtenerTodos(){
            List<ComentarioDTO> comentarios= comentarioService.buscarComentarios();
            if(comentarios.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(comentarios, HttpStatus.OK);
        }
        @GetMapping("/obtenerPorId")
    public ResponseEntity<ComentarioDTO> obtenerPorId(Long id_comentario){
            ComentarioDTO comentarioDTO= comentarioService.buscarComentario(id_comentario);
            if(comentarioDTO==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(comentarioDTO, HttpStatus.OK);
        }
}
