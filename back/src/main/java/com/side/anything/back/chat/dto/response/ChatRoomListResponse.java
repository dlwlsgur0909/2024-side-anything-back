package com.side.anything.back.chat.dto.response;

import com.side.anything.back.chat.entity.ChatParticipant;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class ChatRoomListResponse {

    private List<ChatRoomResponse> chatRoomList;
    private Integer totalPages;

    public ChatRoomListResponse(List<ChatParticipant> chatParticipantList, Integer totalPages) {

        this.chatRoomList = chatParticipantList.stream()
                .map(ChatRoomResponse::new)
                .toList();
        this.totalPages = totalPages;
    }

    @Getter
    private static class ChatRoomResponse {
        private Long chatRoomId;
        private String companionPostTitle;
        private CompanionPostStatus companionPostStatus;

        public ChatRoomResponse(ChatParticipant chatParticipant) {
            this.chatRoomId = chatParticipant.getChatRoom().getId();
            this.companionPostTitle = chatParticipant.getChatRoom().getCompanionPost().getTitle();
            this.companionPostStatus = chatParticipant.getChatRoom().getCompanionPost().getStatus();
        }
    }



}
