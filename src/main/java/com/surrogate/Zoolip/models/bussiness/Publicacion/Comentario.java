package com.surrogate.Zoolip.models.bussiness.Publicacion;

import com.surrogate.Zoolip.models.bussiness.Usuario;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_comentario",nullable = false)
    private Long id_comentario;
    @JoinColumn(name="id_publicacion")
    @ManyToOne(fetch = FetchType.LAZY)
    private Publicacion id_publicacion;
    @Column(name="contenido")
    private String contenido;
    @Column(name="fecha_comentario")
    private LocalDateTime fecha_comentario;
    @JoinColumn(name="id_usuario")
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario id_usuario;

}
