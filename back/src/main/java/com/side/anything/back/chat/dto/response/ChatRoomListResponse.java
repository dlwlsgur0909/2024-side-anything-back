package com.side.anything.back.chat.dto.response;

import com.side.anything.back.chat.entity.ChatParticipant;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ChatRoomListResponse {

    List<ChatRoomResponse> chatRoomList = new ArrayList<>();

    public ChatRoomListResponse(List<ChatParticipant> chatParticipantList) {

        this.chatRoomList = chatParticipantList.stream()
                .map(ChatRoomResponse::new)
                .toList();
    }

    @Getter
    private static class ChatRoomResponse {
        private Long chatRoomId;
        private String companionPostTitle;

        public ChatRoomResponse(ChatParticipant chatParticipant) {
            this.chatRoomId = chatParticipant.getChatRoom().getId();
            this.companionPostTitle = chatParticipant.getChatRoom().getCompanionPost().getTitle();
        }
    }



}
