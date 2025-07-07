package com.side.anything.back.chat.dto.response;

import com.side.anything.back.chat.entity.ChatParticipant;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class ChatRoomListResponse {

    private List<ChatRoomResponse> roomList;
    private Integer totalPages;

    public ChatRoomListResponse(List<ChatParticipant> chatParticipantList, Integer totalPages) {

        this.roomList = chatParticipantList.stream()
                .map(ChatRoomResponse::new)
                .toList();
        this.totalPages = totalPages;
    }

    @Getter
    private static class ChatRoomResponse {
        private Long roomId;
        private String postTitle;
        private CompanionPostStatus postStatus;

        public ChatRoomResponse(ChatParticipant chatParticipant) {
            this.roomId = chatParticipant.getChatRoom().getId();
            this.postTitle = chatParticipant.getChatRoom().getCompanionPost().getTitle();
            this.postStatus = chatParticipant.getChatRoom().getCompanionPost().getStatus();
        }
    }



}
