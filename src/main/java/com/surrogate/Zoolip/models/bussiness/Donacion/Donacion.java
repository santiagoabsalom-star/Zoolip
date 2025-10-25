package com.surrogate.Zoolip.models.bussiness.Donacion;

import com.surrogate.Zoolip.models.bussiness.Institucion.Institucion;
import com.surrogate.Zoolip.models.bussiness.Usuario;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Donacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id_donacion")
    private Long id_donacion;
    @Column(name = "status")
    private ESTATUS status;
    @Column(name="monto")
    private Double monto;
    @Column(name="fecha_inicio")
    private LocalDateTime fecha_inicio;
    @JoinColumn(name = "id_usuario")
    @OneToOne(fetch = FetchType.LAZY)
    private Usuario id_usuario;
    @JoinColumn(name="id_institucion")
    @OneToOne(fetch = FetchType.LAZY)
    private Institucion id_institucion;
}

