package com.side.anything.back.chat.controller;

import com.side.anything.back.chat.dto.request.ChatMessageRequest;
import com.side.anything.back.chat.service.ChatService;
import com.side.anything.back.exception.BasicExceptionResponse;
import com.side.anything.back.exception.CustomException;
import com.side.anything.back.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/{roomId}") // 클라이언트가 서버로 메세지를 보낼 때의 경로
    public void sendMessage(@DestinationVariable Long roomId,
                            @Payload ChatMessageRequest request,
                            Authentication authentication) {

        TokenInfo tokenInfo = (TokenInfo) authentication.getPrincipal();
        chatService.sendMessage(request, tokenInfo);
    }

    // 에러 처리 필요
//    @MessageExceptionHandler(CustomException.class)
//    @SendToUser("/chat/errors")
//    public BasicExceptionResponse handleCustomException(CustomException ce) {
//        return new BasicExceptionResponse(ce);
//    }
}
