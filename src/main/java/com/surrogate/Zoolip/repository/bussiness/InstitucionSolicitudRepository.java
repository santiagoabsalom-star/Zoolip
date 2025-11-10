package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.DTO.InstitucionSolicitudDTO;
import com.surrogate.Zoolip.models.bussiness.Institucion.InstitucionSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InstitucionSolicitudRepository extends JpaRepository<InstitucionSolicitud, Long> {


    @Query("Select new com.surrogate.Zoolip.models.DTO.InstitucionSolicitudDTO(is.id_institucion_solicitud, is.nombre_institucion, is.email_contacto, is.tipo, is.razon_solicitud, is.telefono_contacto, is.estado) from InstitucionSolicitud is")
    List<InstitucionSolicitudDTO> findAllInstitucionSolicitud();
}
