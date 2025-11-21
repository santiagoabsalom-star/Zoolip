package com.surrogate.Zoolip.services.auth;

import com.surrogate.Zoolip.models.DTO.UsuarioDto;
import com.surrogate.Zoolip.models.bussiness.Chat.Chat;
import com.surrogate.Zoolip.models.bussiness.Usuario;
import com.surrogate.Zoolip.models.login.UserPrincipal;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.repository.bussiness.ChatRepository;
import com.surrogate.Zoolip.repository.bussiness.UsuarioRepository;
import com.surrogate.Zoolip.services.auth.JWT.JWTService;
import com.surrogate.Zoolip.services.bussiness.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final JWTService jwtService;
    private final String success;
    private final String error;
    private final ChatRepository chatRepository;
    private final ChatService chatService;


    @Transactional(readOnly = true)
    public List<UsuarioDto> findAvailableUsersInitialized() {

        return usuarioRepository.findAvailableUserDtos();
    }

    public Optional<UsuarioDto> me(String token) {

        if (token == null) {
            return Optional.empty();
        }

        return usuarioRepository.getUserById(jwtService.extractId(token));

    }

    public List<UsuarioDto> findAllByEmail() {

        return usuarioRepository.findAllByEmail(getEmailFromSecurityContext());
    }

    public Optional<Usuario> findById(Long uid) {
        return usuarioRepository.findById(uid);
    }
    @Transactional
    public Response eliminar(Long id_usuario) {
        if (usuarioRepository.existsById(id_usuario)) {
            usuarioRepository.deleteById(id_usuario);
            return new Response("El usuario se ha eliminado correctamente", 200, "Success");
        }
        return new Response("El usuario no existe", 404, "Error");

    }

    public UsuarioDto findDTOById(Long idUsuario) {
        return usuarioRepository.findDTOById(idUsuario);
    }

    public List<UsuarioDto> findAllWithLimit(Long idUsuario) {
        return usuarioRepository.findAllDTosWithLimit(idUsuario);
    }

    public List<UsuarioDto> find5DTOS() {
        return usuarioRepository.find5DTOs();
    }
    @Transactional
    public Response updateCurrentUser(String token, Usuario usuario) {




       Usuario usuarioExistente = usuarioRepository.findById(getIdFromSecurityContext()).orElse(null);

        if (usuarioExistente==null) {
            return new Response(error, 404,"Usuario no encontrado" );
        }





            if(chatRepository.existByNombreLike(usuarioExistente.getNombre())){

                log.info(usuarioExistente.getNombre());
                log.info("El usuario tiene chats asociados, se cambiara el nombre en los chats");
             cambiarNombreChat(usuarioExistente.getNombre(), usuario.getNombre());
            }

            usuarioExistente.setNombre(usuario.getNombre());
            jwtService.InvalidateToken(token);


        if (usuario.getEmail() != null) {
            usuarioExistente.setEmail(usuario.getEmail());
        }
        if (usuario.getRol() != null) {
           return new Response(error, 403, "No puedes camibiar el rol del usuario");
        }
        if(usuario.getBiografia() != null){
            usuarioExistente.setBiografia(usuario.getBiografia());
        }
        if(usuario.getImagenUrl() != null){
            usuarioExistente.setImagenUrl(usuario.getImagenUrl());
        }


       usuarioRepository.actualizarUsuarioCurrent(usuarioExistente);


        return new Response(success, 200, "Usuario actualizado correctamente");
    }

    public UsuarioDto findDTOByIdInstitucion(Long id_institucion) {
        return usuarioRepository.findDTOByInstitucionId(id_institucion);
    }
    private Long getIdFromSecurityContext(){

        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userPrincipal.getId();
    }
    private String getEmailFromSecurityContext(){
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userPrincipal.getEmail();
    }
    private void cambiarNombreChat(String nombreAntiguo, String nombreNuevo){
        if(!chatRepository.existsByNombreChat(nombreAntiguo)){
return;        }
        if(chatRepository.existsByNombreChat(nombreNuevo)){
return;        }

        List<Chat> chats= chatRepository.findBynombreChatLike(nombreAntiguo);
        if(chats.isEmpty()){return;}
        for(Chat chat: chats){
        String[] partes = nombreNuevo.split("_");
        if(partes.length!=2){
return;        }
        if(!chat.getUsuario().getNombre().equals(partes[0]) || !chat.getAdministrador().getNombre().equals(partes[1])){
return;        }
        if(chat.getUsuario().getNombre().equals(partes[0])){
            chat.setNombreChat(nombreNuevo+"_"+chat.getAdministrador().getNombre());
        }
        else{
            chat.setNombreChat(chat.getUsuario().getNombre()+"_"+nombreNuevo);
        }
        chatRepository.save(chat);
}
    }
}
