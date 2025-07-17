package com.side.anything.back.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.side.anything.back.chat.entity.ChatMessage;
import com.side.anything.back.chat.entity.MessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatMessageResponse {

    private Long roomId;
    private Long memberId;
    private String nickname;
    private Long messageId;
    private String message;
    private MessageType messageType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentAt;

    public ChatMessageResponse(ChatMessage chatMessage) {
        this.roomId = chatMessage.getChatRoom().getId();
        this.memberId = chatMessage.getMember().getId();
        this.nickname = chatMessage.getMember().getNickname();
        this.messageId = chatMessage.getId();
        this.message = chatMessage.getMessage();
        this.messageType = chatMessage.getType();
        this.sentAt = chatMessage.getCreatedAt();
    }

}
