package com.side.anything.back.chat.service;

import com.side.anything.back.chat.dto.response.ChatRoomListResponse;
import com.side.anything.back.chat.entity.ChatParticipant;
import com.side.anything.back.chat.entity.ChatRoom;
import com.side.anything.back.chat.repository.ChatParticipantRepository;
import com.side.anything.back.chat.repository.ChatRoomRepository;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import com.side.anything.back.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository roomRepository;
    private final ChatParticipantRepository participantRepository;

    // 채팅방 목록 조회
    public ChatRoomListResponse findChatRoomList(final TokenInfo tokenInfo) {

        return new ChatRoomListResponse(
                participantRepository.findAllByMemberId(tokenInfo.getId(), CompanionPostStatus.DELETED)
        );
    }

}
