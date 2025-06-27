package com.side.anything.back.chat.dto.request;

import lombok.Getter;

@Getter
public class ChatMessageRequest {

    private MessageType messageType;
    private Long roomId;
    private Long memberId;
    private String message;

}
