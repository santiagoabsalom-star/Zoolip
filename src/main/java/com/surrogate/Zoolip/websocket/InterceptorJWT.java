package com.surrogate.Zoolip.websocket;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.surrogate.Zoolip.services.auth.JWT.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.http.WebSocketHandshakeException;
import java.util.Map;
@Component
@Slf4j
@RequiredArgsConstructor
public class InterceptorJWT implements HandshakeInterceptor {
    private final JWTService jwtService;
    private final ObjectMapper objectMapper;
    //todo implementar la validacion del token JWT en el beforeHandshake
    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, @NotNull Map<String, Object> attributes) throws WebSocketHandshakeException {
    return true;
    }
    @Override
    public void afterHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, Exception exception) {

    }
}
