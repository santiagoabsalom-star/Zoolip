package com.surrogate.Zoolip.models.bussiness.Atencion;


import jakarta.persistence.*;

import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Atiende {
    @Id
    @JoinColumn(name = "id_atiende")
    private AtiendeId id_Atiende;
    @Column(name="fecha_inicio")
    private LocalDateTime fecha_inicio;
    @Column(name = "motivo_consulta")
    private String motivo_consulta;
    @Column(name="fecha_final")
    private LocalDateTime fecha_final;

}
