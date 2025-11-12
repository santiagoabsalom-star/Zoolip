package com.surrogate.Zoolip.controllers.Bussines;


import com.surrogate.Zoolip.models.DTO.UsuarioDto;
import com.surrogate.Zoolip.models.bussiness.Usuario;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.services.auth.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/usuario")
public class UsuarioController {
    private final UsuarioService usuarioService;



    @PutMapping("/actualizar")
    public ResponseEntity<Response> actualizarUsuario(@RequestBody Usuario usuario) {
        Response response = usuarioService.actualizar(usuario);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }
    @DeleteMapping("/eliminar")
    public ResponseEntity<Response> eliminarUsuario(@RequestParam Long id_usuario) {
        Response response = usuarioService.eliminar(id_usuario);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @GetMapping("/getUsuarios")
    public ResponseEntity<List<UsuarioDto>> getUsuarios(@RequestParam Long id_usuario) {
        List<UsuarioDto> usuarioDtos = usuarioService.findAllWithLimit(id_usuario);
        if(usuarioDtos==null ||usuarioDtos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuarioDtos);
    }
    @GetMapping("/getUsuarioById")
    public ResponseEntity<UsuarioDto> getUsuarioById(@RequestParam Long id_usuario) {
        UsuarioDto usuarioDto= usuarioService.findDTOById(id_usuario);
        if(usuarioDto == null) {
            return ResponseEntity.notFound().build();

        }
        return  ResponseEntity.ok(usuarioDto);
    }
}
