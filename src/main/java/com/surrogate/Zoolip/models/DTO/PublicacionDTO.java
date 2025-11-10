package com.surrogate.Zoolip.models.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record PublicacionDTO(Long idPublicacion, String contenido, String topico, String nombreUsuario, Integer likes,
                             @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime fechaDudaResuelta, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")LocalDateTime fechaPregunta, LocalDateTime fechaEdicion) {

}
