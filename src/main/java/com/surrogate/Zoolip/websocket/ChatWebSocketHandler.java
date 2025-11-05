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
import java.util.Objects;


@RequiredArgsConstructor
@Slf4j
@Component

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final HashMap<String , String> Usuario_Chat = new HashMap<>();
    //TODO: Implementar la logica del chat (>:D)
    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
    Usuario_Chat.put(Objects.requireNonNull(session.getUri()).getPath(),(String)session.getAttributes().get("nombre"));
    ;


    }
    @Override
    public void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {

    }
    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {


    }
}
