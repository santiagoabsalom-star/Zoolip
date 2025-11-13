package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.DTO.SolicitudAdopcionDTO;
import com.surrogate.Zoolip.models.bussiness.Mascota.SolicitudAdopcion.SolicitudAdopcion;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcion, Long> {
    @Query("select new com.surrogate.Zoolip.models.DTO.SolicitudAdopcionDTO(sp.id_solicitud_adopcion, sp.id_adoptante.nombre, sp.mascota.id, sp.estadoSolicitud, sp.fecha_inicio, sp.fecha_finalizado) from SolicitudAdopcion sp ")
    List<SolicitudAdopcionDTO> findAllDTOs();
    @Query("select new com.surrogate.Zoolip.models.DTO.SolicitudAdopcionDTO(sp.id_solicitud_adopcion, sp.id_adoptante.nombre, sp.mascota.id, sp.estadoSolicitud, sp.fecha_inicio, sp.fecha_finalizado) from SolicitudAdopcion sp where sp.id_solicitud_adopcion =: id_solicitud_adopcion ")
SolicitudAdopcionDTO findDTOById(Long id_solicitud_adopcion);
    @Query("Select count(sp)>0 from SolicitudAdopcion sp where sp.mascota.id=:mascotaId")
    boolean existsByMascotaId(@NotNull Long mascotaId);
}
