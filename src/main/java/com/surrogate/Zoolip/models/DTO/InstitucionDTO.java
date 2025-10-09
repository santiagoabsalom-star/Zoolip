package com.surrogate.Zoolip.models.DTO;

import com.surrogate.Zoolip.models.bussiness.Institucion.Tipo;

import java.time.LocalDateTime;


public interface InstitucionDTO {
    Long getId_institucion();

    String getNombre();

    Tipo getTipo();

    String getDescripcion();

    String getNombreAdministrador();

    String getEmail();

    LocalDateTime getHorario_Inicio();

    LocalDateTime getHorario_Fin();

}
