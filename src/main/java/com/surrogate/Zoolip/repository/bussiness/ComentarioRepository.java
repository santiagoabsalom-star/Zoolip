package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.DTO.ComentarioDTO;
import com.surrogate.Zoolip.models.bussiness.Publicacion.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    @Query("select new com.surrogate.Zoolip.models.DTO.ComentarioDTO(c.id_comentario, c.contenido, c.id_usuario.nombre, c.id_publicacion.id_publicacion) from Comentario c")
    List<ComentarioDTO> findAllComentariosDTO();

    @Query("select new com.surrogate.Zoolip.models.DTO.ComentarioDTO(c.id_comentario, c.contenido, c.id_usuario.nombre, c.id_publicacion.id_publicacion) from Comentario c")
    ComentarioDTO findComentarioDTOById(Long id_comentario);
}
