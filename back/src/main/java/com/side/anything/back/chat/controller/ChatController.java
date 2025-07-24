package com.side.anything.back.chat.controller;

import com.side.anything.back.chat.dto.response.ChatRoomEnterResponse;
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

    // 채팅방 목록 API
    @GetMapping
    public ResponseEntity<ChatRoomListResponse> findChatRoomList(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                 @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                                 @RequestParam(name = "page", defaultValue = "1") int page) {

        return ResponseEntity
                .ok(chatService.findChatRoomList(tokenInfo, keyword, page));
    }

    // 채팅방 상세 API
    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoomEnterResponse> enterChatRoom(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                     @PathVariable Long roomId) {

        return ResponseEntity
                .ok(chatService.enterChatRoom(tokenInfo, roomId));
    }

}
