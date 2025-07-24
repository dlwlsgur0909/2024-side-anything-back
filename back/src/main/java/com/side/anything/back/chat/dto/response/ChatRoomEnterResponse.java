package com.side.anything.back.chat.dto.response;

import com.side.anything.back.chat.entity.ChatMessage;
import com.side.anything.back.chat.entity.ChatParticipant;
import lombok.Getter;

import java.util.List;

@Getter
public class ChatRoomEnterResponse {

    private String postTitle;
    private List<ChatMessageResponse> messageList;
    private List<ChatParticipantResponse> participantList;

    public ChatRoomEnterResponse(String postTitle, List<ChatParticipant> participantList, List<ChatMessage> messageList) {
        this.postTitle = postTitle;
        this.messageList = messageList.stream()
                .map(ChatMessageResponse::new)
                .toList();
        this.participantList = participantList.stream()
                .map(ChatParticipantResponse::new)
                .toList();


    }

    @Getter
    private static class ChatParticipantResponse {

        private Long memberId;
        private String nickname;
        private String gender;
        private Boolean isHost;

        public ChatParticipantResponse(ChatParticipant chatParticipant) {
            this.memberId = chatParticipant.getMember().getId();
            this.nickname = chatParticipant.getMember().getNickname();
            this.gender = chatParticipant.getMember().getGender();
            this.isHost = chatParticipant.getIsHost();
        }
    }

}
