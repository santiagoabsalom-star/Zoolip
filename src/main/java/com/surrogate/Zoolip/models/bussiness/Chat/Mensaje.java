package com.surrogate.Zoolip.models.bussiness.Chat;

import com.surrogate.Zoolip.models.bussiness.Usuario;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Mensaje {
    @Id
    @Column(name = "id_mensaje", nullable = false, unique = true)
    private Long id_mensaje;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chat", nullable = false)
    private Chat id_chat;
    @Column(name = "contenido", nullable = false)
    private String contenido;
    @Column(name="fecha_hora", nullable = false)
    private LocalDateTime fechaHora;
    @JoinColumn(name="emisor", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario emisor;
    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario receptor;


}
