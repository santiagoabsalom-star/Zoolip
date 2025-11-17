package com.surrogate.Zoolip.models.bussiness.Mascota.SolicitudAdopcion;

import com.surrogate.Zoolip.models.bussiness.Mascota.Mascota;
import com.surrogate.Zoolip.models.bussiness.Usuario;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Data
@Entity
@NoArgsConstructor
public class SolicitudAdopcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_solicitud_adopcion",nullable = false)
    private Long id_solicitud_adopcion;
    @JoinColumn(name="id_adoptante", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario id_adoptante;
    @JoinColumn(name="id_mascota",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Mascota mascota;
    @Column(name="motivo_decision")
    private String motivo_decision;
    @Column(name="razon_solicitud",nullable = false)
    private String razon_solicitud;
    @Column(name = "estado_solicitud",nullable = false)
    private EstadoSolicitud estadoSolicitud;
    @Column(name="fecha_inicio")
    private LocalDateTime fecha_inicio;
    @Column(name="fecha_finalizado")
    private LocalDateTime fecha_finalizado;
}
