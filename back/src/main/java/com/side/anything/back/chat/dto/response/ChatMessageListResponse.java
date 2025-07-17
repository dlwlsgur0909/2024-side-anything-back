package com.side.anything.back.chat.dto.response;

import com.side.anything.back.chat.entity.ChatMessage;
import lombok.Getter;

import java.util.List;

@Getter
public class ChatMessageListResponse {

    private String postTitle;
    private List<ChatMessageResponse> messageList;

    public ChatMessageListResponse(String postTitle, List<ChatMessage> messageList) {
        this.postTitle = postTitle;
        this.messageList = messageList.stream()
                .map(ChatMessageResponse::new)
                .toList();
    }

}
