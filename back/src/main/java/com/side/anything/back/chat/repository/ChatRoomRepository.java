package com.side.anything.back.chat.repository;

import com.side.anything.back.chat.entity.ChatRoom;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query(
            """
            SELECT cr FROM ChatRoom cr
            WHERE
                cr.id = :roomId
                AND cr.isActive = true
            """
    )
    Optional<ChatRoom> findChatRoom(@Param("roomId") Long roomId);

    @Query(
            """
            SELECT cr FROM ChatRoom cr
            WHERE
                cr.companionPost.id = :postId
                AND cr.isActive = true
            """
    )
    Optional<ChatRoom> findChatRoomByPost(@Param("postId") Long postId);

    @Query(
            """
            SELECT cr FROM ChatRoom cr
            JOIN FETCH
                cr.companionPost post
            WHERE
                cr.id = :roomId
                AND cr.isActive = true
                AND post.status != :postStatus
            """
    )
    Optional<ChatRoom> findWithPost(@Param("roomId") Long roomId,
                                    @Param("postStatus")CompanionPostStatus postStatus);
}
