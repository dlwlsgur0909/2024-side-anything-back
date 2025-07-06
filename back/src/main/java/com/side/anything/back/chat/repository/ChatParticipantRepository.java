package com.side.anything.back.chat.repository;

import com.side.anything.back.chat.entity.ChatParticipant;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    @Query(
            """
            SELECT COUNT(cp) > 0 FROM ChatParticipant cp
            WHERE
                cp.chatRoom.id = :roomId
                AND cp.chatRoom.isActive = true
                AND cp.member.id = :memberId
                AND cp.isActive = true
            """
    )
    Boolean isParticipant(@Param("roomId")Long roomId, @Param("memberId")Long memberId);


    @Query(
            """
            SELECT cp FROM ChatParticipant cp
            JOIN FETCH
                cp.chatRoom cr
            JOIN FETCH
                cr.companionPost post
            WHERE
                cp.isActive = true
                AND cp.member.id = :memberId
                AND cr.isActive = true
                AND post.status != :postStatus
            ORDER BY
                cp.id DESC
            """
    )
    List<ChatParticipant> findAllByMemberId(@Param("memberId")Long memberId,
                                            @Param("postStatus")CompanionPostStatus postStatus);

}
