package com.side.anything.back.chat.service;

import com.side.anything.back.chat.dto.response.ChatRoomListResponse;
import com.side.anything.back.chat.entity.ChatParticipant;
import com.side.anything.back.chat.repository.ChatParticipantRepository;
import com.side.anything.back.chat.repository.ChatRoomRepository;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import com.side.anything.back.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private static final int SIZE = 5;

    private final ChatRoomRepository roomRepository;
    private final ChatParticipantRepository participantRepository;

    // 채팅방 목록 조회
    public ChatRoomListResponse findChatRoomList(final TokenInfo tokenInfo,
                                                 final String keyword,
                                                 final int page) {

        PageRequest pageRequest = PageRequest.of(page - 1, SIZE);

        Page<ChatParticipant> pagedChatParticipantList = participantRepository
                .findParticipantList(keyword, tokenInfo.getId(), CompanionPostStatus.DELETED, pageRequest);

        return new ChatRoomListResponse(pagedChatParticipantList.getContent(), pagedChatParticipantList.getTotalPages());
    }

}
