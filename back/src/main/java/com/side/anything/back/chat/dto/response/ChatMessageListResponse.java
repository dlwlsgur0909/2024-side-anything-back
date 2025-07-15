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

}
