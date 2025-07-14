package com.side.anything.back.chat.entity;

import com.side.anything.back.base.BaseTimeEntity;
import com.side.anything.back.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private MessageType type;

    public static ChatMessage of(ChatRoom chatRoom, Member member, String message, MessageType type) {

        ChatMessage chatMessage = new ChatMessage();

        chatMessage.chatRoom = chatRoom;
        chatMessage.member = member;
        chatMessage.message = message;
        chatMessage.type = type;

        return chatMessage;
    }

}
