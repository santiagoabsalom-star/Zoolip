package com.surrogate.Zoolip.models.DTO;

import com.surrogate.Zoolip.models.bussiness.Institucion.Tipo;

import java.time.LocalDateTime;


public interface InstitucionDTO {
    public Long getId_institucion();
    public String getNombre();
    public Tipo getTipo();
    public String getDescripcion();
    public String getNombreAdministrador();
    public String getEmail();
    public LocalDateTime getHorario_Inicio();
    public LocalDateTime getHorario_Fin();

}
