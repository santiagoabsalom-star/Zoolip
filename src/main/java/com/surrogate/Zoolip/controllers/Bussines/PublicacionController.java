package com.surrogate.Zoolip.controllers.Bussines;

import com.surrogate.Zoolip.models.DTO.PublicacionDTO;
import com.surrogate.Zoolip.models.bussiness.Publicacion.Publicacion;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.services.bussiness.PublicacionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/publicacion")
public class PublicacionController {
    private final PublicacionService publicacionService;

    @PostMapping("/crear")
    public ResponseEntity<Response> crearPublicacion(@RequestBody Publicacion publicacion) {

        Response response = publicacionService.crear(publicacion);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @PostMapping("/actualizar")
    public ResponseEntity<Response> actualizarPublicacion(@RequestBody Publicacion publicacion) {
        Response response = publicacionService.actualizar(publicacion);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<Response> eliminarPublicacion(@RequestBody Long id_publicacion) {
        Response response = publicacionService.eliminar(id_publicacion);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @GetMapping(value = "/obtenerTodas", produces = "Application/json")
    public ResponseEntity<List<PublicacionDTO>> obtenerTodas(@RequestParam long id_publicacion) {
        List<PublicacionDTO> publicaciones = publicacionService.obtenerTodasPaginacion(id_publicacion);
        if (publicaciones == null || publicaciones.isEmpty()) {
            return ResponseEntity.ofNullable(null);
        }
        return ResponseEntity.ok(publicaciones);
    }

    @GetMapping("/obtenerPublicacionesPublicas")
    public ResponseEntity<List<PublicacionDTO>> obtenerPublicacionesPublicas() {
        List<PublicacionDTO> publicaciones = publicacionService.obtenerPublicacionesPublicas();
        if (publicaciones == null || publicaciones.isEmpty()) {
            return ResponseEntity.ofNullable(null);
        }
        return ResponseEntity.ok(publicaciones);
    }

    @GetMapping("/obtenerPorId")
    public ResponseEntity<PublicacionDTO> obtenerPorId(@RequestParam Long id_publicacion) {
        PublicacionDTO publicacion = publicacionService.obtenerPorId(id_publicacion);
        if (publicacion == null) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(publicacion);
    }
    @GetMapping("obtenerFavUsuario")
    public ResponseEntity<List<PublicacionDTO>> obtenerFavUsuario(@RequestParam Long id_usuario) {
        List<PublicacionDTO> publicaciones = publicacionService.obtenerTodasPorFavUsuario(id_usuario);
        if (publicaciones == null || publicaciones.isEmpty()) {
            return ResponseEntity.ofNullable(null);
        }
        return ResponseEntity.ok(publicaciones);
    }
@GetMapping("/obtenerPorUsuario")
public ResponseEntity<List<PublicacionDTO>> obtenerPorUsuario(@RequestParam Long id_usuario) {
    List<PublicacionDTO> publicaciones = publicacionService.obtenerPorUsuario(id_usuario);
    if (publicaciones == null || publicaciones.isEmpty()) {
        return ResponseEntity.ofNullable(null);
    }
    return ResponseEntity.ok(publicaciones);
}
    @GetMapping("/obtenerPorUsuarioCurrent")
    public ResponseEntity<List<PublicacionDTO>> obtenerPorUsuarioCurrent(HttpServletRequest request) {
        String token=getTokenFromRequest(request);
        List<PublicacionDTO> publicaciones = publicacionService.obtenerPorUsuarioCurrent(token);
        if (publicaciones == null || publicaciones.isEmpty()) {
            return ResponseEntity.ofNullable(null);
        }
        return ResponseEntity.ok(publicaciones);
    }
    @PostMapping("/putPublicacionFav")
    public ResponseEntity<Response> putPublicacionFav(@RequestParam Long id_publicacion, @RequestParam Long id_usuario) {
        Response response = publicacionService.putPublicacionFav(id_publicacion, id_usuario);
        return ResponseEntity.status(response.getHttpCode()).body(response);
}

    @DeleteMapping("/deletePublicacionFav")
    public ResponseEntity<Response> deletePublicacionFav(@RequestParam Long id_publicacion, @RequestParam Long id_usuario) {
        Response response = publicacionService.deletePublicacionFav(id_publicacion, id_usuario);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }
    private String getTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() == null || request.getCookies().length == 0) {

            return null;
        }

        return Objects.requireNonNull(Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("AUTH_TOKEN")).findFirst().orElse(null)).getValue();
    }
}