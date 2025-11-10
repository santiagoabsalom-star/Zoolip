package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.DTO.PublicacionDTO;
import com.surrogate.Zoolip.models.bussiness.Publicacion.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
                FROM Publicacion p where p.id_publicacion>= :id_publicacion
                            ORDER BY p.id_publicacion LIMIT 10
            """)
    List<PublicacionDTO> findAllPublicacionesDTO(long id_publicacion);

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
                FROM Publicacion p where p.id_publicacion = :id_publicacion
            """)
    PublicacionDTO findPublicacionDTOById(Long id_publicacion);


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
                FROM Publicacion p JOIN PublicacionFavUsuario f ON p.id_publicacion = f.id_publicacion.id_publicacion
                WHERE f.id_usuario.id = :idUsuario
               \s
           \s""")
    List<PublicacionDTO> findAllPublicacionesDTOByFavUsuario(Long idUsuario);
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
                ORDER BY p.id_publicacion LIMIT 10
           """)
    List<PublicacionDTO> findPublicacionesPublicas();
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
    WHERE LOWER(p.contenido) LIKE LOWER(CONCAT('%', :contenido, '%'))
""")
    List<PublicacionDTO> findPublicacionDTOByContenido(@Param("contenido") String contenido);


}

