package com.surrogate.Zoolip.models.bussiness.Atencion;


import com.surrogate.Zoolip.models.bussiness.Mascota.Mascota;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Atiende {
    @EmbeddedId
    private AtiendeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idDiagnostico")
    @JoinColumn(name = "id_diagnostico", nullable = false)
    private Diagnostico diagnostico;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idVeterinario")
    @JoinColumn(name = "id_veterinario", nullable = false)
    private Veterinario veterinario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idMascota")
    @JoinColumn(name = "id_mascota", nullable = false)
    private Mascota mascota;
    @Column(name = "fecha_inicio")
    private LocalDateTime fecha_inicio;
    @Column(name = "motivo_consulta")
    private String motivo_consulta;
    @Column(name = "fecha_final")
    private LocalDateTime fecha_final;


    public Atiende() {
    }

    public Atiende(Diagnostico diagnostico, Veterinario veterinario, Mascota mascota,
                   LocalDateTime fecha_inicio, String motivo_consulta, LocalDateTime fecha_final) {
        this.diagnostico = diagnostico;
        this.veterinario = veterinario;
        this.mascota = mascota;
        this.fecha_inicio = fecha_inicio;
        this.motivo_consulta = motivo_consulta;
        this.fecha_final = fecha_final;

        this.id = new AtiendeId(
                diagnostico.getId_diagnostico(),
                veterinario.getId_veterinario(),
                mascota.getId()
        );
    }
}
