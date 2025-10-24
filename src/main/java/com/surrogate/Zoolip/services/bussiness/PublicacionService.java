package com.surrogate.Zoolip.services.bussiness;

import com.surrogate.Zoolip.models.DTO.PublicacionDTO;
import com.surrogate.Zoolip.models.bussiness.Publicacion.Publicacion;
import com.surrogate.Zoolip.models.bussiness.Usuario;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.repository.bussiness.PublicacionRepository;
import com.surrogate.Zoolip.repository.bussiness.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicacionService {
        private final PublicacionRepository publicacionRepository;
        private final UsuarioRepository usuarioRepository;

        public Response crear(Publicacion publicacion) {
            publicacion.setId_usuario(verifyUser(publicacion.getId_usuario().getId()));
            Response response= verifyPublicacion(publicacion);
            if(response.getHttpError()==200){
                publicacionRepository.save(publicacion);
                return response;
            }
            return response;

        }
        public Response actualizar(Publicacion publicacion) {
            Response response= verifyPublicacion(publicacion);
            if(response.getHttpError()==200){
                publicacionRepository.saveAndFlush(publicacion);
            }
            return response;
        }
        public Response eliminar(Long id_publicacion) {
            if(publicacionRepository.existsById(id_publicacion)){
                publicacionRepository.deleteById(id_publicacion);
                return new Response("success", 200, "El publicacion se ha eliminado correctamente");
                        }
            return new Response("success", 404, "El publicacion no existe");

        }
        @Transactional
        public List<PublicacionDTO> obtenerTodas(){
            return publicacionRepository.findAllPublicacionesDTO();
        }
        @Transactional
        public PublicacionDTO obtenerPorId(Long id_publicacion) {
            return publicacionRepository.findPublicacionDTOById(id_publicacion);
        }

        public Response verifyPublicacion(@NotNull Publicacion publicacion){
            assert publicacion.getId_usuario().getId()!=null;
log.info("Id de usuario que hizo la publicacion: {}" , publicacion.getId_usuario().getId());
            if (!usuarioRepository.existsById(publicacion.getId_usuario().getId())) {
                return new Response("error", 403, "El usuario no existe o no se encuentra");
            }

                if(publicacion.getContenido() == null){
                    return new Response("error",403, "El comentario no puede no tener contenido mongoloid" );
                }

            int wordCount = publicacion.getContenido().trim().split("\\s+").length;

            if (wordCount > 200) {
                return new Response("error", 403, "El comentario no puede tener más de 200 palabras (" + wordCount + " encontradas)");
            }
            return new Response("success", 200, "Operacion hecha con exito");
        }
        private Usuario verifyUser(Long id_usuario){
            return usuarioRepository.findById(id_usuario).orElse(null);

        }

        }



