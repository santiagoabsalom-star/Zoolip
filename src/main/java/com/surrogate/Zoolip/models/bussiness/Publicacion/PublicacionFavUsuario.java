package com.surrogate.Zoolip.models.bussiness.Publicacion;

import com.surrogate.Zoolip.models.bussiness.Usuario;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity

public class PublicacionFavUsuario {
    @Id
    @Column(name = "id_publicacion_fav_usuario", nullable = false, unique = true)
    private Long id_publicacion_fav_usuario;
    @JoinColumn(name="id_publicacion")
    @ManyToOne(fetch=FetchType.LAZY)
    private Publicacion id_publicacion;
    @JoinColumn(name="id_usuario")
    @ManyToOne(fetch=FetchType.LAZY)
    private Usuario id_usuario;
}
