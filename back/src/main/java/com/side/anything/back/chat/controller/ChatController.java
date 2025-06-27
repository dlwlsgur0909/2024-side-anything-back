package com.side.anything.back.chat.controller;

import com.side.anything.back.chat.dto.request.ChatMessageRequest;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    @MessageMapping("/chat.{roomId}") // 클라이언트가 서버로 메세지를 보낼 때의 경로
    @SendTo("/sub/chat/{roodId}")
    public ChatMessageRequest sendMessage(@DestinationVariable Long roomId, @Payload ChatMessageRequest request) {
        // 서비스 로직 호출 필요
    }

}
