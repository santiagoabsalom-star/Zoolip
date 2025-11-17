package com.surrogate.Zoolip.websocket;

import com.surrogate.Zoolip.models.bussiness.Chat.Chat;
import com.surrogate.Zoolip.models.bussiness.Chat.Mensaje;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.repository.bussiness.ChatRepository;
import com.surrogate.Zoolip.repository.bussiness.UsuarioRepository;
import com.surrogate.Zoolip.services.bussiness.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.util.HashMap;


@RequiredArgsConstructor
@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {


    private final HashMap<String, WebSocketSession> sesiones= new HashMap<>();
    private final ChatService chatService;
    private final UsuarioRepository usuarioRepository;
    private final ChatRepository chatRepository;

    private final String error;
    private final String success;
    //TODO: Implementar la logica del chat (>:D)

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        String nombreChat= session.getAttributes().get("NombreChat").toString();
        String nombreUsuario= session.getAttributes().get("Nombre").toString();
        String[] usuarios= nombreChat.split("_");
        String emisor= usuarios[0];
        String receptor= usuarios[1];
        if (!chatRepository.existsByNombreChat(nombreChat) && !(usuarioRepository.findByNombre(emisor).getRol().equals("ROLE_ADMINISTRADOR"))){

            Response response =creaChatIfNotExists(emisor, receptor);
            if (response.getHttpCode() != 200) {
                log.error("Error al crear el chat: {}", response.getMessage());
                session.close(CloseStatus.BAD_DATA);
                log.error("Conexion cerrada por error al crear el chat");
            }
        }
        log.info("Nueva conexión WebSocket establecida para el chat: {} ", nombreChat);
        sesiones.put(nombreUsuario, session);

    }

    @Override
    public void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {


            String nombreChat= session.getAttributes().get("NombreChat").toString();

            String[] usuarios = nombreChat.split("_");
            String receptor;
            if(usuarios[0].equals(session.getAttributes().get("Nombre").toString())){
                receptor= usuarios[1];
            }else{
            receptor= usuarios[0];
            }




            WebSocketSession sesionReceptor= sesiones.get(receptor);

            if(sesionReceptor!=null && sesionReceptor.isOpen()){sesionReceptor.sendMessage(message);
                guardarMensaje(message, nombreChat, session.getAttributes().get("Nombre").toString(), receptor);
            log.info("Mensaje enviado al receptor: {}", receptor);

            }
        guardarMensaje(message, nombreChat, session.getAttributes().get("Nombre").toString(), receptor);





    }

    private void guardarMensaje(@NotNull TextMessage message, String nombreChat, String emisor, String receptor) {
        if(!chatRepository.existsByNombreChat(nombreChat)){

            log.error("El chat no existe, no se puede guardar el mensaje");
        }
        Chat chat= chatRepository.findBynombreChat(nombreChat);
        Mensaje mensaje= new Mensaje();
        mensaje.setContenido(message.getPayload());
        mensaje.setId_chat(chat);
        mensaje.setFechaHora(LocalDateTime.now());
        mensaje.setEmisor(usuarioRepository.findByNombre(emisor));
        mensaje.setReceptor(usuarioRepository.findByNombre(receptor));
        Response response= chatService.guardarMensaje(mensaje);
        if(response.getHttpCode()!=200){
            log.error("Error guardando el mensaje: {}", response.getMessage());
        }else {
            log.info("Mensaje guardado correctamente");
        }

    }


    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {


    }

    private Response creaChatIfNotExists(String emisor, String receptor) {
        Chat chat = new Chat();
        chat.setNombreChat(emisor + "_" + receptor);
        if (!usuarioRepository.existsByNombre(emisor) || !usuarioRepository.existsByNombre(receptor)) {
            return new Response(error, 404, "Uno de los usuarios no existe");
        }
        chat.setUsuario(usuarioRepository.findByNombre(emisor));
        chat.setAdministrador(usuarioRepository.findByNombre(receptor));

        chatService.crearChat(chat);
        return new Response(success, 200, "Chat creado correctamente");
    }



}





