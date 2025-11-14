package com.surrogate.Zoolip.services.bussiness;

import com.surrogate.Zoolip.models.DTO.PublicacionDTO;
import com.surrogate.Zoolip.models.bussiness.Publicacion.Publicacion;
import com.surrogate.Zoolip.models.bussiness.Publicacion.Tipo;
import com.surrogate.Zoolip.models.bussiness.Usuario;
import com.surrogate.Zoolip.models.login.UserPrincipal;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.repository.bussiness.PublicacionFavUsuarioRepository;
import com.surrogate.Zoolip.repository.bussiness.PublicacionRepository;
import com.surrogate.Zoolip.repository.bussiness.UsuarioRepository;
import com.surrogate.Zoolip.services.auth.JWT.JWTService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicacionService {
    private final PublicacionRepository publicacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final Random random= new Random();
    private final JWTService jwtService;
    private final PublicacionFavUsuarioRepository publicacionFavUsuarioRepository;
    private final String error;
    private final String success;


    public Response crear(Publicacion publicacion) {
        UserPrincipal user= (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        publicacion.setId_usuario(verifyUser(user.getId()));
        Response response = verifyPublicacion(publicacion);
        if (response.getHttpCode() == 200) {
            publicacionRepository.save(publicacion);
            return response;
        }
        return response;

    }

    public Response actualizar(Publicacion publicacion) {
        Response response = verifyPublicacion(publicacion);
        if (response.getHttpCode() == 200) {
            publicacionRepository.saveAndFlush(publicacion);
        }
        return response;
    }

    public Response eliminar(Long id_publicacion) {
        if (publicacionRepository.existsById(id_publicacion)) {
            publicacionRepository.deleteById(id_publicacion);
            return new Response(success, 200, "El publicacion se ha eliminado correctamente");
        }
        return new Response(success, 404, "El publicacion no existe");

    }

    @Transactional
    public List<PublicacionDTO> obtenerTodasPaginacion(long id_publicacion) {
        return publicacionRepository.findAllPublicacionesDTO(id_publicacion);
    }

    public List<PublicacionDTO> obtenerTodasPorFavUsuario(Long id_usuario) {
        return publicacionRepository.findAllPublicacionesDTOByFavUsuario(id_usuario);
    }
    @Transactional
    public PublicacionDTO obtenerPorId(Long id_publicacion) {
        return publicacionRepository.findPublicacionDTOById(id_publicacion);
    }

    public Response verifyPublicacion(@NotNull Publicacion publicacion) {
        if (publicacion.getId_usuario() == null) {
            return new Response(error, 404, "El usuario no existe");
        }

        log.info("Id de usuario que hizo la publicacion: {}", publicacion.getId_usuario().getId());
        if (!usuarioRepository.existsById(publicacion.getId_usuario().getId())) {
            return new Response(error, 403, "El usuario no existe o no se encuentra");
        }

        if (publicacion.getContenido() == null) {
            return new Response(error, 403, "El comentario no puede no tener contenido mongoloid");
        }
        if(publicacion.getTipo() == Tipo.CONSULTA && publicacion.getImagen_url()!=null) {
            return new Response(error, 403, "Una publicacion de tipo consulta no puede tener imagenes");
        }

        int wordCount = publicacion.getContenido().trim().split("\\s+").length;

        if (wordCount > 200) {
            return new Response(error, 403, "El comentario no puede tener más de 200 palabras (" + wordCount + " encontradas)");
        }
        return new Response(success, 200, "Operacion hecha con exito");
    }

    private Usuario verifyUser(Long id_usuario) {

        return usuarioRepository.findById(id_usuario).orElse(null);

    }

    public Response putPublicacionFav(Long idPublicacion, Long idUsuario) {
        if (!publicacionRepository.existsById(idPublicacion)) {
            return new Response(error, 404, "La publicacion no existe");
        }
        if (!usuarioRepository.existsById(idUsuario)) {
            return new Response(error, 404, "El usuario no existe");
        }
        publicacionFavUsuarioRepository.addFavorite(idPublicacion, idUsuario);
        return new Response(success, 200, "Publicacion añadida a favoritos");
    }

    public Response deletePublicacionFav(Long idPublicacion, Long idUsuario) {
        if (!publicacionRepository.existsById(idPublicacion)) {
            return new Response(error, 404, "La publicacion no existe");
        }
        if (!usuarioRepository.existsById(idUsuario)) {
            return new Response(error, 404, "El usuario no existe");
        }
        publicacionFavUsuarioRepository.removeFavorite(idPublicacion, idUsuario);
        return new Response(success, 200, "Publicacion eliminada de favoritos");
    }

    public List<PublicacionDTO> obtenerPublicacionesPublicas() {

        return publicacionRepository.findPublicacionesPublicas(random.nextLong(0, (publicacionRepository.count())));
    }
    public List<PublicacionDTO> obtenerPublicacionesLike(String contenido){
        log.info("Buscando publicaciones con contenido similar a: {}", contenido);
        return publicacionRepository.findPublicacionDTOByContenido(contenido);
    }

    public List<PublicacionDTO> obtenerPorUsuario(Long idUsuario) {
        return publicacionRepository.findPublicacionDTOByIdUsuario(idUsuario);
    }

    public List<PublicacionDTO> obtenerPorUsuarioCurrent(String token) {

        return publicacionRepository.findPublicacionDTOByIdUsuario(jwtService.extractId(token));
    }
}



