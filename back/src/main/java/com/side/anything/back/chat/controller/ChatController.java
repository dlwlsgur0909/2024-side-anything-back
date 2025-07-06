package com.side.anything.back.chat.controller;

import com.side.anything.back.chat.dto.response.ChatRoomListResponse;
import com.side.anything.back.chat.service.ChatService;
import com.side.anything.back.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<ChatRoomListResponse> findChatRoomList(@AuthenticationPrincipal TokenInfo tokenInfo) {

        return ResponseEntity
                .ok(chatService.findChatRoomList(tokenInfo));
    }

}
