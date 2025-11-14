package com.surrogate.Zoolip.websocket;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.surrogate.Zoolip.models.DTO.PublicacionDTO;
import com.surrogate.Zoolip.services.bussiness.PublicacionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor

public class PostWebSocketHandler extends TextWebSocketHandler {
    private final PublicacionService publicacionService;
    private final ConcurrentHashMap<String, WebSocketSession> sesiones = new ConcurrentHashMap<>();
    private final ObjectMapper mapper=getMapper();

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        sesiones.put(session.getId(),session);


        log.info("Nueva conexión WebSocket establecida con id :{} ", session.getId());

    }
    @Override
    public void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {
        String mensaje = message.getPayload();

        List<PublicacionDTO> publicaciones = publicacionService.obtenerPublicacionesLike(mensaje);
        String publicacionesJson = mapper.writeValueAsString(publicaciones);
        WebSocketSession sesion = sesiones.get(session.getId());

        if (sesion != null && sesion.isOpen()) {
            sesion.sendMessage(new TextMessage(publicacionesJson));
        }

        }



@Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        sesiones.remove(session.getId());

        log.info("Conexión WebSocket cerrada con estado {}", status);
    }
private ObjectMapper getMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    mapper.disable(MapperFeature.REQUIRE_HANDLERS_FOR_JAVA8_TIMES);
    return mapper;
    }

}
