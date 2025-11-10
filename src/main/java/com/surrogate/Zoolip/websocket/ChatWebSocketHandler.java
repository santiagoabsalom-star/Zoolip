package com.surrogate.Zoolip.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


@RequiredArgsConstructor
@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
        private final HashMap<String , String> chat_sesiones= new HashMap<>();
        private final HashSet<WebSocketSession> sesiones = new HashSet<>();

        //TODO: Implementar la logica del chat (>:D)

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        sesiones.add(session);
        String sesion_id = session.getId();

        String nombre_chat= (String) session.getAttributes().get("chat_name");
    chat_sesiones.put(nombre_chat, sesion_id );
    }
    @Override
    public void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {

        Iterator<WebSocketSession> iterator = sesiones.iterator();
        iterator.forEachRemaining(sesion-> {
            try {
                if(!sesion.getId().equals(session.getId())){
                    if(sesion.isOpen()){
                        if(chat_sesiones.containsKey(sesion.getAttributes().get("chat_name"))){
                        log.info("Enviando mensaje al chat: {}", sesion.getAttributes().get("chat_name"));
                            sesion.sendMessage(message);
                        }
                    }

                    }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {


    }
}
