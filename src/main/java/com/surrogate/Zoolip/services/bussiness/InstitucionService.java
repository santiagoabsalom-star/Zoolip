package com.surrogate.Zoolip.services.bussiness;
import com.surrogate.Zoolip.models.DTO.InstitucionDTO;
import com.surrogate.Zoolip.models.bussiness.Institucion.Institucion;
import com.surrogate.Zoolip.models.bussiness.Usuario;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.repository.bussiness.InstitucionRepository;
import com.surrogate.Zoolip.repository.bussiness.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class InstitucionService {
    private final InstitucionRepository institucionRepository;
    private final UsuarioRepository usuarioRepository;

    
    public Response crear(Institucion institucion) {
        Response response = comprobarInst(institucion);
        log.info("El nombre de la institucion es: {}", institucion.getNombre());
        if (Objects.equals(response.getStatus(), "success")) {
            Usuario usuario= usuarioRepository.findById(institucion.getId_usuario().getId()).orElse(null);
            institucion.setId_usuario(usuario);
            institucionRepository.save(institucion);
            return new Response("success", "Institucion creada");
        }
        return response;
    }
        public Response actualizar(Institucion institucion){
            Response response = comprobarInst(institucion);
            if(response.getStatus().equals("success")){
                institucionRepository.save(institucion);
                return new Response("success", "Institucion actualizada");
            }
            return response;
        }
        public Response eliminar(Long id_institucion){
        try {
            institucionRepository.deleteById(id_institucion);
            return new Response("success", "Institucion eliminada");
        }catch (Exception e){
            log.error(e);
            return new Response("error", "Error eliminando institucion");
        }
        }



        public List<InstitucionDTO> getInstituciones(){
        return institucionRepository.findAllDTO();


        }

        public InstitucionDTO getInstitucion(Long id){
        if(!institucionRepository.existsById(id)){
            return null;
        }
        return institucionRepository.findInstitucionDTOById(id);
        }
    public Response comprobarInst(Institucion institucion) {
        if(institucion.getId_usuario()!=null){
            if(!usuarioRepository.existsById(institucion.getId_usuario().getId())){
                log.info("Usuario no encontrado");
                return new Response("error", "Usuario no encontrado");
            }
            institucion.setId_usuario(usuarioRepository.findById(institucion.getId_usuario().getId()).orElse(null));

        }
        if(institucionRepository.findByU(institucion.getId_usuario().getId())!=null){
            log.info("Este administrador ya administra una institucion");
            return new Response("error", "Este administrador ya administra una institucion");
        }

        if(institucion.getNombre()==null){

            log.info("El nombre no puede ser vacio");
        return new Response("error", "El nombre no puede ser vacio");
        }
        if(institucion.getTipo() == null){
            log.info("El tipo no puede ser vacio");
        return new Response("error", "El tipo no puede ser vacio");
        }

        if(institucionRepository.existsById(institucion.getId_institucion())){
            log.info("{} ya existe", institucion.getNombre());
            return new Response("error", "La institucion ya existe");
        }
        if(institucionRepository.existsByNombre(institucion.getNombre())){
            log.info("{} ya existe", institucion.getNombre());
            return new Response("error", "La institucion ya existe");

        }
        if(!institucion.getId_usuario().getRol().equals("ADMINISTRADOR")  ){
            log.info("El rol del usuario tiene que ser ADMINISTRADOR" );
            return new Response("error", "El rol del usuario tiene que ser administrador");

        }
        return new Response("success");
    }
}
