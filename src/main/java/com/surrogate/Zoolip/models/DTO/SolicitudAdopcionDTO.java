package com.surrogate.Zoolip.models.DTO;

import com.surrogate.Zoolip.models.bussiness.Mascota.SolicitudAdopcion.EstadoSolicitud;

import java.time.LocalDateTime;

public record SolicitudAdopcionDTO(Long idSolicitudAdopcion, String nombreSolicitante, Long idMascota,
                                   EstadoSolicitud estadoSolicitud, LocalDateTime fecha_inicio, LocalDateTime fecha_finalizado) {
}
