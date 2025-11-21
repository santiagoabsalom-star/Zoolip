package com.surrogate.Zoolip.models.DTO;


import com.surrogate.Zoolip.models.bussiness.Publicacion.Tipo;

import java.time.LocalDateTime;

public record PublicacionDTO(Long idPublicacion, String avatarUrl,String imagenUrl, long idUsuario, Tipo tipo, String contenido, String topico, String nombreUsuario, Integer likes,
                             LocalDateTime fechaDudaResuelta, LocalDateTime fechaPregunta, LocalDateTime fechaEdicion) {

}
