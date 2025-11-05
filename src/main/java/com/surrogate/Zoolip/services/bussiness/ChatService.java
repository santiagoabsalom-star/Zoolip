package com.surrogate.Zoolip.services.bussiness;

import com.surrogate.Zoolip.models.DTO.ChatDTO;
import com.surrogate.Zoolip.models.DTO.MensajeDTO;
import com.surrogate.Zoolip.models.bussiness.Chat.Chat;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.repository.bussiness.ChatRepository;
import com.surrogate.Zoolip.repository.bussiness.MensajeRepository;
import com.surrogate.Zoolip.repository.bussiness.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final ChatRepository chatRepository;
    private final MensajeRepository mensajeRepository;
    private final String success;
    private final String error;
    private final UsuarioRepository usuarioRepository;

    public Response crearChat(Chat chat){
        Response response = verificarChat(chat);
        if(response.getHttpCode()!=200){
            return response;
        }
        chat.setNombreChat(chat.getUsuario().getNombre()+"_"+chat.getAdministrador().getNombre());
        chatRepository.save(chat);
        return response;
    }
    public Response verificarChat(Chat chat){
        if( Thread.currentThread().getStackTrace()[1].getMethodName().equals("crearChat") && chatRepository.existByNombre(chat.getUsuario().getNombre()+"_"+chat.getAdministrador().getNombre())){
        return new Response(error, 409, "El chat ya existe");

    }
        if(chat.getAdministrador().getNombre().equals(chat.getUsuario().getNombre())){
            return new Response(error, 409, "No puedes crear un chat contigo mismo");

        }

        if(Objects.equals(chat.getUsuario().getRol(), "ADMINISTRADOR") || Objects.equals(chat.getAdministrador().getRol(), "USUARIO")){
            return new Response(error, 409, "El rol de los usuarios no es correcto");


        }
        return new Response(success, 200, "Operacion hecha correctamente");

    }
    //Este metodo se va a usar para actualizar el nombre del chat en caso de que se cambie el nombre de algun usuario
    public Response actualizarChat(Chat chat){
        Response response = verificarChat(chat);
        if(response.getHttpCode()!=200){
            return response;
        }
        chat.setNombreChat(chat.getUsuario().getNombre()+"_"+chat.getAdministrador().getNombre());
        chatRepository.save(chat);
        return response;

    }
    public Response eliminarChat(Chat chat){
        if(!chatRepository.existByNombre(chat.getNombreChat())){
            return new Response(error, 404, "El chat no existe");

        }
        if(!chatRepository.existsById(chat.getId_chat())){
            return new Response(error, 404, "El chat no existe");

        }

       if(!(mensajeRepository.deleteAllByIdChat(chat.getId_chat())>0)){

        return new Response(error,500,"Error eliminando los mensajes del chat" );

       }
        chatRepository.delete(chat);
        return new Response(success, 200, "Chat eliminado correctamente");
    }

    public List<MensajeDTO>obtenerMensajesDeChat(Long idChat){
        if(!chatRepository.existsById(idChat)){
            return null;
        }
        return mensajeRepository.findAllByChatId(idChat);
    }
    public List<ChatDTO> obtenerChatsDeUsuario(Long idUsuario){
        if(!usuarioRepository.existsById(idUsuario)){
            return null;
        }
        return chatRepository.findAllByIdUsuario(idUsuario);


    }






}
