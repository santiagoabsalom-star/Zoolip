package com.surrogate.Zoolip.repository.bussiness;

import com.surrogate.Zoolip.models.DTO.MensajeDTO;
import com.surrogate.Zoolip.models.bussiness.Chat.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {



    @Modifying
    @Query("delete from Mensaje m where m.chat.id_chat= :idChat")
    int deleteAllByIdChat(Long idChat);
    @Query("Select new com.surrogate.Zoolip.models.DTO.MensajeDTO(m.id_mensaje, m.chat.id_chat, m.chat.nombreChat, m.emisor.nombre, m.receptor.nombre) from Mensaje m where m.chat.id_chat=:idChat")
    List<MensajeDTO> findAllByChatId(Long idChat);
}
