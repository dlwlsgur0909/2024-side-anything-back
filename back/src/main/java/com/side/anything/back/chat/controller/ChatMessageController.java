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

    /*
    Authentication 대신 Principal을 바로 받으면 TokenInfo 객체가 아니라
    Spring 내부에서 생성한 Principal 타입의 구현체인 org.springframework.security.core.userdetails.User 깉은 객체가 들어간다
    Authentication으로 받아 getPrincipal 메서드를 사용하면 StompJwtChannelInterceptor에서 설정한 TokenInfo 객체가 들어온다
     */
    @MessageMapping("/chat/{roomId}") // 클라이언트가 서버로 메세지를 보낼 때의 경로
    public void sendMessage(@DestinationVariable Long roomId,
                            @Payload ChatMessageRequest request,
                            Authentication authentication) {

        TokenInfo tokenInfo = (TokenInfo) authentication.getPrincipal();
        messageService.sendMessage(tokenInfo, roomId, request);
    }

    // 에러 처리 필요
//    @MessageExceptionHandler(CustomException.class)
//    @SendToUser("/chat/errors")
//    public BasicExceptionResponse handleCustomException(CustomException ce) {
//        return new BasicExceptionResponse(ce);
//    }
}
