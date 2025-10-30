package com.surrogate.Zoolip.models.DTO;

import java.time.LocalDateTime;

public record PublicacionDTO(Long idPublicacion, String contenido, String topico, String nombreUsuario, Integer likes,
                             LocalDateTime fechaDudaResuelta, LocalDateTime fechaPregunta, LocalDateTime fechaEdicion) {

}
