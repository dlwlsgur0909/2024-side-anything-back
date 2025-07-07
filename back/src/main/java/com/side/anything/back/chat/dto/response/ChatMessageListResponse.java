package com.side.anything.back.chat.dto.response;

import com.side.anything.back.chat.entity.ChatMessage;
import lombok.Getter;

import java.util.List;

@Getter
public class ChatMessageListResponse {

    private List<ChatMessageResponse> messageList;

    public ChatMessageListResponse(List<ChatMessage> messageList) {
        this.messageList = messageList.stream()
                .map(ChatMessageResponse::new)
                .toList();
    }

    @Getter
    private static class ChatMessageResponse {

        private Long messageId;
        private String message;
        private Long memberId;
        private String nickname;

        public ChatMessageResponse(ChatMessage chatMessage) {
            this.messageId = chatMessage.getId();
            this.message = chatMessage.getMessage();
            this.memberId = chatMessage.getMember().getId();
            this.nickname = chatMessage.getMember().getNickname();
        }

    }

}
