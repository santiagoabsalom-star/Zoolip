package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.DTO.InstitucionDTO;
import com.surrogate.Zoolip.models.bussiness.Institucion.Institucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface InstitucionRepository extends JpaRepository<Institucion, Long> {
    @Query("SELECT i.id_institucion as Id_Institucion" +
            ", i.id_usuario.nombre as NombreUsuario" +
            ", i.nombre as Nombre" +
            ", i.tipo as Tipo" +
            ", i.email as Email" +
            ",i.horario_inicio as Horario_Inicio" +
            ", i.horario_fin as Horario_fin FROM Institucion i")
    List<InstitucionDTO> findAllDTO();

    @Query(value = "SELECT i.id_institucion as Id_Institucion" +
            ", i.id_usuario.nombre as NombreUsuario" +
            ", i.nombre as Nombre" +
            ", i.tipo as Tipo" +
            ", i.email as Email" +
            ",i.horario_inicio as Horario_Inicio" +
            ", i.horario_fin as Horario_fin FROM Institucion i where Institucion.id_institucion = :id_institucion", nativeQuery = true)
    InstitucionDTO findInstitucionDTOById(@Param("id_institucion") Long id_institucion);

    @Query("Select  i.nombre from Institucion i where i.id_usuario.id = :id_usuario ")
     String findByU(@Param("id_usuario") Long id_usuario);

    boolean existsByNombre(String nombre);
}
