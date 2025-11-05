package com.surrogate.Zoolip.models.bussiness.Chat;

import com.surrogate.Zoolip.models.bussiness.Usuario;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity

public class Chat {
    @Id
    @Column(name = "id_chat", nullable = false, unique = true)
    private Long id_chat;
    @Column(name="nombre_chat", nullable = false)
    private String nombreChat;
    @JoinColumn(name="usuario", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;
    @JoinColumn(name="administrador", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario administrador;


}
