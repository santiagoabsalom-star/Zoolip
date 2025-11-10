package com.surrogate.Zoolip.config;

import com.surrogate.Zoolip.websocket.ChatWebSocketHandler;
import com.surrogate.Zoolip.websocket.InterceptorJWT;
import com.surrogate.Zoolip.websocket.PostWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final PostWebSocketHandler postWebSocketHandler;
    private final ChatWebSocketHandler chatWebSocketHandler;
    private final InterceptorJWT interceptorJWT;
    @Override
    public void registerWebSocketHandlers(@NotNull WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/chat/**")
                .addHandler(postWebSocketHandler, "/post/**")
                .addInterceptors(interceptorJWT)
                .setAllowedOrigins("*");

    }
}
