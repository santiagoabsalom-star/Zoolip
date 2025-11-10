package com.surrogate.Zoolip.models.DTO;

import com.surrogate.Zoolip.models.bussiness.Institucion.Estado;
import com.surrogate.Zoolip.models.bussiness.Institucion.Tipo;

public record InstitucionSolicitudDTO(long idSolicitud, String nombreInstitucion, String emailContacto, Tipo tipoSolicitud, String razonSolicitud, String telefonoContacto, Estado estadoSolicitud ) {
}
