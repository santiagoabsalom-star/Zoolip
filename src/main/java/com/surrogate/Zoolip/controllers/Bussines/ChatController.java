package com.surrogate.Zoolip.controllers.Bussines;

import com.surrogate.Zoolip.models.DTO.ChatDTO;
import com.surrogate.Zoolip.models.DTO.MensajeDTO;
import com.surrogate.Zoolip.models.bussiness.Chat.Chat;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.services.bussiness.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@Slf4j
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/crearChat")
    public ResponseEntity<Response> crearChat(@RequestBody Chat chat) {
        Response response = chatService.crearChat(chat);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @DeleteMapping("/eliminarChat")
    public ResponseEntity<Response> eliminarChat(@RequestBody Chat chat) {
        Response response = chatService.eliminarChat(chat);
        return ResponseEntity.status(response.getHttpCode()).body(response);


    }
    @GetMapping("/obtenerChatsPorUsuario")
    public ResponseEntity<List<ChatDTO>> obtenerChatsPorUsuario(@RequestParam Long id_usuario) {

        List<ChatDTO> chats = chatService.obtenerChatsDeUsuario(id_usuario);
        if (chats == null || chats.isEmpty()) {
            return ResponseEntity.ofNullable(null);
        }
        return ResponseEntity.ok(chats);
    }
    @GetMapping("/obtenerMensajesPorChat")
    public ResponseEntity<List<MensajeDTO>> obtenerMensajesPorChat(@RequestParam Long id_chat) {
        List<MensajeDTO> mensajes = chatService.obtenerMensajesDeChat(id_chat);
        if (mensajes == null || mensajes.isEmpty()) {
            return ResponseEntity.ofNullable(null);
        }
        return ResponseEntity.ok(mensajes);
    }


}


