package com.surrogate.Zoolip.services.bussiness;

import com.surrogate.Zoolip.models.bussiness.Atencion.Veterinario;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.repository.bussiness.AtiendeRepository;
import com.surrogate.Zoolip.repository.bussiness.InstitucionRepository;

import com.surrogate.Zoolip.repository.bussiness.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VeterinarioService {

            private final InstitucionRepository institucionRepository;
            private final VeterinarioRepository veterinarioRepository;
            public Response crear(Veterinario veterinario) {
                Response response= verifyInstitucion(veterinario);
                if(response.getHttpError()==200){
                    veterinarioRepository.save(veterinario);

                }
                return response;
            }
            public Response actualizar(Veterinario veterinario) {
                Response response= verifyInstitucion(veterinario);
                if(response.getHttpError()==200){
                    veterinarioRepository.save(veterinario);
                }
                return response;

            }
            public Response eliminar(Veterinario veterinario) {
                if(veterinarioRepository.existsById(veterinario.getId_veterinario())){
                    veterinarioRepository.deleteById(veterinario.getId_veterinario());
                    return new Response("success", 200, "Veterinario eliminado");

                }
                return new Response("error", 404, "Veterinario no encontrado");
            }
            /**TODO
                DTOS, OBTENERTODOS, y OBTENERPORID
             **/

            private Response verifyInstitucion(Veterinario veterinario) {
                if(veterinario.getNombre() == null) {return new Response("error", 409, "El nombre no puede ser nulo");}
                if (institucionRepository.existsById(veterinario.getId_institucion().getId_institucion())) {

                    veterinario.setId_institucion(institucionRepository.findById(veterinario.getId_institucion().getId_institucion()).orElse(null));
                    return new Response("success", 200, "Proceso exitoso");
                }
                return new Response("error", 404, "La institucion no existe");



            }

}
