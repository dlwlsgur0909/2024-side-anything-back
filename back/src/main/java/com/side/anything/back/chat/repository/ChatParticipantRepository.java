package com.side.anything.back.chat.repository;

import com.side.anything.back.chat.entity.ChatParticipant;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
            value = """
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
                        AND post.title LIKE %:keyword%
                    ORDER BY
                        cp.id DESC
                    """,
            countQuery = """
                        SELECT COUNT(cp) FROM ChatParticipant cp
                        JOIN
                            cp.chatRoom cr
                        JOIN
                            cr.companionPost post
                        WHERE
                            cp.isActive = true
                            AND cp.member.id = :memberId
                            AND cr.isActive = true
                            AND post.status != :postStatus
                            AND post.title LIKE %:keyword%
                        """
    )
    Page<ChatParticipant> findParticipantList(@Param("keyword") String keyword,
                                              @Param("memberId")Long memberId,
                                              @Param("postStatus")CompanionPostStatus postStatus,
                                              Pageable pageable);

}
