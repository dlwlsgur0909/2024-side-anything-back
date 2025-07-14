package com.side.anything.back.chat.dto.request;

import com.side.anything.back.chat.entity.MessageType;
import lombok.Getter;

@Getter
public class ChatMessageRequest {

    private MessageType messageType;
    private String message;

}
