package com.surrogate.Zoolip.events;

import com.surrogate.Zoolip.models.bussiness.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

@Slf4j
public class UsuarioCreado extends ApplicationEvent {
    public UsuarioCreado(Usuario usuario) {

        super(usuario);
        log.info("Usuario Creado");
    }
}
