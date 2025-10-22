package com.surrogate.Zoolip.models.bussiness.Institucion;

import com.surrogate.Zoolip.models.bussiness.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "institucion", schema = "zoolip")
public class Institucion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_institucion", unique = true, nullable = false)
    private long id_institucion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", unique = true, nullable = false)
    private Usuario id_usuario;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "tipo", nullable = false)
    private Tipo tipo;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "horario_inicio", nullable = false)
    private LocalTime horario_inicio;

    @Column(name = "horario_fin", nullable = false)
    private LocalTime horario_fin;


}
