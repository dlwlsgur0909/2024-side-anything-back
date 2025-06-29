package com.side.anything.back.chat.repository;

import com.side.anything.back.chat.entity.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    @Query(
            """
            SELECT COUNT(cp) FROM ChatParticipant cp
            WHERE
                cp.chatRoom.id = :roomId
                AND cp.chatRoom.isActive = true
                AND cp.member.id = :memberId
                AND cp.isActive = true
            """
    )
    Boolean isParticipant(Long roomID, Long memberId);


}
