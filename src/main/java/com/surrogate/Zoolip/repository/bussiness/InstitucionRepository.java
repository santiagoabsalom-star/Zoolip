package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.DTO.InstitucionDTO;
import com.surrogate.Zoolip.models.bussiness.Institucion.Institucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface InstitucionRepository extends JpaRepository<Institucion, Long> {
    @Query("SELECT i.id_institucion as id_institucion" +
            ", i.id_usuario.nombre as nombreAdministrador" +
            ", i.nombre as nombre" +
            ", i.tipo as tipo" +
            ",i.descripcion as descripcion"+
            ", i.email as email" +
            ",i.horario_inicio as horario_Inicio" +
            ", i.horario_fin as horario_Fin FROM Institucion i")
    List<InstitucionDTO> findAllDTO();

    @Query(value = "SELECT i.id_institucion as id_institucion" +
            ", i.id_usuario.nombre as nombreAdministrador" +
            ", i.nombre as nombre" +
            ", i.tipo as tipo" +
            ", i.descripcion as descripcion"+
            ", i.email as email" +
            ",i.horario_inicio as horario_Inicio" +
            ", i.horario_fin as horario_Fin FROM Institucion i where i.id_institucion = :id_institucion")
    InstitucionDTO findInstitucionDTOById(@Param("id_institucion") Long id_institucion);

    @Query("Select  i.nombre from Institucion i where i.id_usuario.id = :id_usuario ")
     String findByU(@Param("id_usuario") Long id_usuario);

    boolean existsByNombre(String nombre);
}
