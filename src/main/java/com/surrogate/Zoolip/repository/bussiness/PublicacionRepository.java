package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.DTO.PublicacionDTO;
import com.surrogate.Zoolip.models.bussiness.Publicacion.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    @Query("""
                SELECT new com.surrogate.Zoolip.models.DTO.PublicacionDTO(
                    p.id_publicacion,
                    p.contenido,
                    p.topico,
                    p.id_usuario.nombre,
                    p.likes,
                    p.fecha_duda_resuelta,
                    p.fecha_pregunta,
                    p.fecha_edicion
                )
                FROM Publicacion p
            """)
    List<PublicacionDTO> findAllPublicacionesDTO();

    @Query("""
                SELECT new com.surrogate.Zoolip.models.DTO.PublicacionDTO(
                    p.id_publicacion,
                    p.contenido,
                    p.topico,
                    p.id_usuario.nombre,
                    p.likes,
                    p.fecha_duda_resuelta,
                    p.fecha_pregunta,
                    p.fecha_edicion
                )
                FROM Publicacion p where p.id_publicacion = :idPublicacion
            """)
    PublicacionDTO findPublicacionDTOById(Long idPublicacion);
}
