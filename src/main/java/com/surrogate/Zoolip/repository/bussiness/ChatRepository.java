package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.DTO.ChatDTO;
import com.surrogate.Zoolip.models.bussiness.Chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    
    @Query("Select new com.surrogate.Zoolip.models.DTO.ChatDTO(c.id_chat, c.nombreChat, c.usuario.nombre, c.administrador.nombre) from Chat c where c.usuario.id=:idUsuario")
    List<ChatDTO> findAllByIdUsuario(Long idUsuario);

    @Query("Select count(*) > 0 from Chat c where c.nombreChat = :nombreChat")
    boolean existsByNombreChat(String nombreChat);

    Chat findBynombreChat(String nombreChat);


}
