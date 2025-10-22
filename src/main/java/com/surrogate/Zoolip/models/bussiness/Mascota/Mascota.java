package com.surrogate.Zoolip.models.bussiness.Mascota;

import com.surrogate.Zoolip.models.bussiness.Institucion.Institucion;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Entity
@Table(name = "mascota", schema = "zoolip")
public class Mascota {
    @Id
    @Column(name = "id_mascota", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "tamanio", nullable = false)
    private Tamanio tamanio;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_adopcion", nullable = false)
    private EstadoAdopcion estadoAdopcion;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_salud", nullable = false)
    private EstadoSalud estadoSalud;
    @Column(name = "edad", nullable = false)
    private Integer edad;
    @Column(name = "raza", nullable = false)
    private String raza;
    @Column(name = "especie", nullable = false)
    private String especie;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_institucion", nullable = false)
    private Institucion id_institucion;

}
