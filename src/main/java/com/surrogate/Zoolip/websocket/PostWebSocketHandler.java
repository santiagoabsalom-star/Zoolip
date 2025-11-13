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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor

public class PostWebSocketHandler extends TextWebSocketHandler {
    private final PublicacionService publicacionService;
    private final HashSet<WebSocketSession> sesiones = new HashSet<>();
    private final ObjectMapper mapper=getMapper();

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        sesiones.add(session);


        log.info("Nueva conexión WebSocket establecida con id :{} ", session.getId());

    }
    @Override
    public void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {
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
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        sesiones.remove(session);

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
