package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.DTO.ChatDTO;
import com.surrogate.Zoolip.models.bussiness.Chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c from Chat c WHERE c.nombreChat = :nombre")
    boolean existByNombre(String nombre);
    @Query("Select new com.surrogate.Zoolip.models.DTO.ChatDTO(c.id_chat, c.nombreChat, c.usuario.nombre, c.administrador.nombre) from Chat c where c.usuario.id=:idUsuario")
    List<ChatDTO> findAllByIdUsuario(Long idUsuario);

}
