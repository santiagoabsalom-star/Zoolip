package com.surrogate.Zoolip.models.DTO;

import java.time.LocalDateTime;

public record AtiendeDTO(Long idDiagnostico, Long idMascota, Long idVeterinario, LocalDateTime fechaInicio,
                         String motivoConsulta, LocalDateTime fechaFinal) {
}
