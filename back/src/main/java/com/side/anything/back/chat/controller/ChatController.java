package com.side.anything.back.chat.controller;

import com.side.anything.back.chat.dto.request.ChatMessageRequest;
import com.side.anything.back.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/{roomId}") // 클라이언트가 서버로 메세지를 보낼 때의 경로
    public void sendMessage(@DestinationVariable Long roomId, @Payload ChatMessageRequest request) {
        chatService.sendMessage(request);
    }

}
