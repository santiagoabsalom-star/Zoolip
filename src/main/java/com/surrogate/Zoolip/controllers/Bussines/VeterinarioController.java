package com.surrogate.Zoolip.controllers.Bussines;

import com.surrogate.Zoolip.models.DTO.VeterinarioDTO;
import com.surrogate.Zoolip.models.bussiness.Atencion.Veterinario;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.services.bussiness.VeterinarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/veterinario")
public class VeterinarioController {
    private final VeterinarioService veterinarioService;


    @PostMapping("/crear")
    public ResponseEntity<Response> crearVeterinario(@RequestBody Veterinario veterinario) {
        Response response = veterinarioService.crear(veterinario);
        return ResponseEntity.status(response.getHttpCode()).body(response);

    }
    @PostMapping("/actualizar")
    public ResponseEntity<Response> actualizarVeterinario(@RequestBody Veterinario veterinario) {
        Response response = veterinarioService.actualizar(veterinario);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }
    @DeleteMapping("/eliminar")
    public ResponseEntity<Response> eliminarVeterinario(@RequestBody Long id_veterinario) {
        Response response = veterinarioService.eliminar(id_veterinario);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }
    @GetMapping("/obtenerTodos")
    public ResponseEntity<List<VeterinarioDTO>> getVeterinarios() {
        List<VeterinarioDTO> veterinarioDTOS= veterinarioService.obtenerVeterinarios();
        if(veterinarioDTOS.isEmpty()){
            return ResponseEntity.ofNullable(null);

        }
        return ResponseEntity.ok(veterinarioDTOS);
    }
    @GetMapping("/obtenerPorId")
    public ResponseEntity<VeterinarioDTO> getVeterinarioById(@RequestParam Long id_veterinario) {
        VeterinarioDTO veterinarioDTO= veterinarioService.obtenerVeterinario(id_veterinario);
        if(veterinarioDTO==null){
            return ResponseEntity.ofNullable(null);
        }
        return ResponseEntity.ok(veterinarioDTO);
    }
}
