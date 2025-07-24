package com.side.anything.back.chat.service;

import com.side.anything.back.chat.dto.response.ChatRoomEnterResponse;
import com.side.anything.back.chat.dto.response.ChatRoomListResponse;
import com.side.anything.back.chat.entity.ChatMessage;
import com.side.anything.back.chat.entity.ChatParticipant;
import com.side.anything.back.chat.entity.ChatRoom;
import com.side.anything.back.chat.entity.MessageType;
import com.side.anything.back.chat.repository.ChatMessageRepository;
import com.side.anything.back.chat.repository.ChatParticipantRepository;
import com.side.anything.back.chat.repository.ChatRoomRepository;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import com.side.anything.back.exception.CustomException;
import com.side.anything.back.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.side.anything.back.exception.BasicExceptionEnum.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private static final int SIZE = 5;

    private final ChatRoomRepository roomRepository;
    private final ChatParticipantRepository participantRepository;
    private final ChatMessageRepository messageRepository;

    // 채팅방 목록 조회
    public ChatRoomListResponse findChatRoomList(final TokenInfo tokenInfo,
                                                 final String keyword,
                                                 final int page) {

        PageRequest pageRequest = PageRequest.of(page - 1, SIZE);

        Page<ChatParticipant> pagedChatParticipantList = participantRepository
                .searchMyChatRoomList(keyword, tokenInfo.getId(), CompanionPostStatus.DELETED, pageRequest);

        return new ChatRoomListResponse(pagedChatParticipantList.getContent(), pagedChatParticipantList.getTotalPages());
    }

    // 채팅 내역과 참가자 조회
    public ChatRoomEnterResponse enterChatRoom(final TokenInfo tokenInfo, final Long chatRoomId) {

        Boolean isParticipant = participantRepository.isParticipant(chatRoomId, tokenInfo.getId());

        if(!isParticipant) {
            throw new CustomException(NOT_FOUND, "채팅방을 찾을 수 없습니다");
        }

        // 채팅방 조회
        ChatRoom findChatRoom = roomRepository.findWithPost(chatRoomId, CompanionPostStatus.DELETED)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "존재하지 않는 동행입니다"));

        /*
        채팅방 조회 시 동행 상태와 채팅방의 active를 확인하기 때문에 여기서는 확인하지 않아도 되겠지만
        참가자 조회를 다른 곳에서 사용할 수 도 있어서 한번 더 검증
         */
        // 채팅방 참가자 조회
        List<ChatParticipant> participantList = participantRepository.findParticipantList(chatRoomId, CompanionPostStatus.DELETED);

        // 메세지 내역 조회
        List<ChatMessage> messageList = messageRepository.findMessageList(chatRoomId, tokenInfo.getId(), MessageType.ENTER);

        return new ChatRoomEnterResponse(findChatRoom.getCompanionPost().getTitle(), participantList, messageList);
    }




}
