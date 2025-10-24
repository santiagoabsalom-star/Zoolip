package com.surrogate.Zoolip.services.bussiness;

import com.surrogate.Zoolip.models.DTO.ComentarioDTO;
import com.surrogate.Zoolip.models.bussiness.Publicacion.Comentario;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.repository.bussiness.ComentarioRepository;
import com.surrogate.Zoolip.repository.bussiness.PublicacionRepository;
import com.surrogate.Zoolip.repository.bussiness.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComentarioService  {
    private final ComentarioRepository comentarioRepository;
    private final PublicacionRepository publicacionRepository;
    private final UsuarioRepository usuarioRepository;

    public Response comentar(Comentario comentario){
        Response response = verificarComentario(comentario);
        if(response.getHttpError()==200) {
            comentario.setFecha_comentario(LocalDateTime.now());
            comentarioRepository.save(comentario);
            return response;
        }
        return response;
    }
    public Response actualizar(Comentario comentario){
        Response response = verificarComentario(comentario);

        if(response.getHttpError()==200) {


            comentarioRepository.save(comentario);
            return response;
        }
        return response;
    }
    public Response eliminar(Comentario comentario){
        Response response = verificarComentario(comentario);
        if(response.getHttpError()==200) {
            comentarioRepository.delete(comentario);
            return response;
        }
        return response;
    }
    @Transactional
    public List<ComentarioDTO> buscarComentarios(){
    return comentarioRepository.findAllComentariosDTO();

    }
    public ComentarioDTO BuscarComentario(Long id_comentario){
    return comentarioRepository.findComentarioDTOById(id_comentario);
    }
    public Response verificarComentario(Comentario comentario){
            if(!verifyUsuario(comentario.getId_usuario().getId())) return new Response("error", 404, "Usuario no encontrado");
            if(!verifyPublicacion(comentario.getId_publicacion().getId_publicacion())) return new Response("error", 404, "Publicacion no encontrada");
            if(comentario.getContenido()==null) return new Response("error",403,"El contenido del mensaje no puede ser nulo");
            return new Response("success", 200, "Operacion hecha con exito");
    }
public boolean verifyPublicacion(Long id_publicacion){
        return publicacionRepository.existsById(id_publicacion);
}
public boolean verifyUsuario(Long id_usuario){
        return usuarioRepository.existsById(id_usuario);
}

}