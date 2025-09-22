package com.surrogate.Zoolip.models.DTO;

import com.surrogate.Zoolip.models.bussiness.Institucion.Tipo;

import java.util.Date;

public interface InstitucionDTO {
    public long getId_institucion();
    public String getNombre();
    public Tipo getTipo();
    public String getDescripcion();
    public String getNombreUsuario();
    public String getEmail();
    public Date getHorario_Inicio();
    public Date getHorario_Fin();

}
