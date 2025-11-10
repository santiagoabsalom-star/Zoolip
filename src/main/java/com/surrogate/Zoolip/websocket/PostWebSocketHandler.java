package com.surrogate.Zoolip.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surrogate.Zoolip.models.DTO.PublicacionDTO;
import com.surrogate.Zoolip.services.bussiness.PublicacionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor

public class PostWebSocketHandler extends TextWebSocketHandler {
    private final PublicacionService publicacionService;
    private final HashSet<WebSocketSession> sesiones = new HashSet<>();
    private final ObjectMapper mapper;
    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {

        sesiones.add(session);
        String nombre_usuario = (String) session.getAttributes().get("username");

        log.info("Nueva conexión WebSocket establecida para el usuario:{} ", nombre_usuario);

    }
    @Override
    public void handleTextMessage(@NotNull WebSocketSession session, @NotNull org.springframework.web.socket.TextMessage message) throws Exception {
        String nombre_usuario = (String) session.getAttributes().get("username");
        log.info("Mensaje recibido del usuario {}: {}", nombre_usuario, message.getPayload());
        String mensaje = message.getPayload();
        List<PublicacionDTO> publicaciones = publicacionService.obtenerPublicacionesLike(mensaje);
        String publicacionesJson = mapper.writeValueAsString(publicaciones);
        sesiones.iterator().forEachRemaining(sesion -> {
            try {
                if (sesion.isOpen() && session.getId().equals(sesion.getId())) {
                    sesion.sendMessage(new TextMessage(publicacionesJson));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });


    }
@Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull org.springframework.web.socket.CloseStatus status) throws Exception {
        sesiones.remove(session);
        String nombre_usuario = (String) session.getAttributes().get("username");
        log.info("Conexión WebSocket cerrada para el usuario: " + nombre_usuario + " con estado: " + status);
    }

}
