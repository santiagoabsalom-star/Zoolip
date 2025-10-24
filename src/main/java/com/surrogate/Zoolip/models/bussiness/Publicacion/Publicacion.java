package com.surrogate.Zoolip.models.bussiness.Publicacion;

import com.surrogate.Zoolip.models.bussiness.Usuario;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Publicacion {
    @Id
    @Column(name = "id_publicacion", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_publicacion;
    @JoinColumn(name="id_usuario")
    @ManyToOne(fetch=FetchType.LAZY)
    private Usuario id_usuario;
    @Column(name="topico")
    private String topico;
    @Column(name="contenido")
    private String contenido;
    @Column(name="likes")
    private Integer likes;
    @Column(name="fecha_edicion")
    private LocalDateTime fecha_edicion;
    @Column(name="fecha_duda_resuelta" )
    private LocalDateTime fecha_duda_resuelta;
    @Column(name="fecha_pregunta")
    private LocalDateTime fecha_pregunta;
}

