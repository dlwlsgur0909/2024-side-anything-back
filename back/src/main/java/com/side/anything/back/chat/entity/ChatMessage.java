package com.side.anything.back.chat.entity;

import com.side.anything.back.base.BaseTimeEntity;
import com.side.anything.back.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "message")
    private String message;

    public static ChatMessage of(ChatRoom chatRoom, Member member, String message) {

        ChatMessage chatMessage = new ChatMessage();

        chatMessage.chatRoom = chatRoom;
        chatMessage.member = member;
        chatMessage.message = message;

        return chatMessage;
    }

}
