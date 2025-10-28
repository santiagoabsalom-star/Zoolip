package com.surrogate.Zoolip.models.DTO;

import com.surrogate.Zoolip.models.bussiness.Donacion.ESTATUS;

import java.time.LocalDateTime;

public record DonacionDTO(long idDonacion, double monto, LocalDateTime fechaInicio, ESTATUS estado, String nombreUsuario, String nombreInstitucion) {
}
