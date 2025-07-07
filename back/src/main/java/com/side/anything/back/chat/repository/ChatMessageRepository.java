package com.side.anything.back.chat.repository;

import com.side.anything.back.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query(
            """
            SELECT cm FROM ChatMessage cm
            JOIN FETCH
                cm.member m
            WHERE
                cm.chatRoom.id = :roomId
            ORDER BY
                cm.id ASC
            """
    )
    List<ChatMessage> findMessageList(@Param("roomId")Long roomId);

}
