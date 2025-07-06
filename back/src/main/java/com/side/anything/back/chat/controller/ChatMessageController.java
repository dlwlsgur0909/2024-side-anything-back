package com.side.anything.back.chat.controller;

import com.side.anything.back.chat.dto.request.ChatMessageRequest;
import com.side.anything.back.chat.service.ChatMessageService;
import com.side.anything.back.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService messageService;

    @MessageMapping("/chat/{roomId}") // 클라이언트가 서버로 메세지를 보낼 때의 경로
    public void sendMessage(@DestinationVariable Long roomId,
                            @Payload ChatMessageRequest request,
                            Authentication authentication) {

        TokenInfo tokenInfo = (TokenInfo) authentication.getPrincipal();
        messageService.sendMessage(request, tokenInfo);
    }

    // 에러 처리 필요
//    @MessageExceptionHandler(CustomException.class)
//    @SendToUser("/chat/errors")
//    public BasicExceptionResponse handleCustomException(CustomException ce) {
//        return new BasicExceptionResponse(ce);
//    }
}
