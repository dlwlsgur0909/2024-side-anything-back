package com.side.anything.back.chat.repository;

import com.side.anything.back.chat.entity.ChatMessage;
import com.side.anything.back.chat.entity.MessageType;
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
                AND cm.createdAt >=
                (
                    SELECT MAX(cm2.createdAt) FROM ChatMessage cm2
                    WHERE
                        cm2.chatRoom.id = :roomId
                        AND cm2.member.id = :memberId
                        AND cm2.type = :type
                )
            ORDER BY
                cm.createdAt ASC
            """
    )
    List<ChatMessage> findMessageList(@Param("roomId")Long roomId,
                                      @Param("memberId") Long memberId,
                                      @Param("type")MessageType type);

}
