package com.side.anything.back.chat.controller;

import com.side.anything.back.chat.dto.response.ChatMessageListResponse;
import com.side.anything.back.chat.dto.response.ChatRoomListResponse;
import com.side.anything.back.chat.service.ChatService;
import com.side.anything.back.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<ChatRoomListResponse> findChatRoomList(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                 @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                                 @RequestParam(name = "page", defaultValue = "1") int page) {

        return ResponseEntity
                .ok(chatService.findChatRoomList(tokenInfo, keyword, page));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ChatMessageListResponse> findChatMessageList(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                       @PathVariable Long roomId) {

        return ResponseEntity
                .ok(chatService.findChatMessageList(tokenInfo, roomId));
    }

}
