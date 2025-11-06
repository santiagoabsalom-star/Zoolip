package com.surrogate.Zoolip.models.DTO;

import com.surrogate.Zoolip.models.bussiness.Institucion.Tipo;

import java.time.LocalDateTime;
import java.time.LocalTime;


public interface InstitucionDTO {
    Long getId_institucion();

    String getNombre();

    Tipo getTipo();

    String getDescripcion();

    String getNombreAdministrador();

    String getEmail();

    LocalTime getHorario_Inicio();

    LocalTime getHorario_Fin();

}
