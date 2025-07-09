package com.side.anything.back.chat.service;

import com.side.anything.back.chat.dto.request.ChatMessageRequest;
import com.side.anything.back.chat.dto.response.ChatMessageResponse;
import com.side.anything.back.chat.entity.ChatMessage;
import com.side.anything.back.chat.entity.ChatRoom;
import com.side.anything.back.chat.repository.ChatMessageRepository;
import com.side.anything.back.chat.repository.ChatParticipantRepository;
import com.side.anything.back.chat.repository.ChatRoomRepository;
import com.side.anything.back.config.RedisPublisher;
import com.side.anything.back.exception.CustomException;
import com.side.anything.back.member.entity.Member;
import com.side.anything.back.member.repository.MemberRepository;
import com.side.anything.back.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.side.anything.back.exception.BasicExceptionEnum.FORBIDDEN;
import static com.side.anything.back.exception.BasicExceptionEnum.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private final ChatRoomRepository roomRepository;
    private final ChatParticipantRepository participantRepository;
    private final ChatMessageRepository messageRepository;
    private final MemberRepository memberRepository;

    private final RedisPublisher redisPublisher;

    @Transactional
    public void sendMessage(final TokenInfo tokenInfo, final Long roomId, final ChatMessageRequest request) {

        // 사용자 조회
        Member findMember = memberRepository.findMemberById(tokenInfo.getId())
                .orElseThrow(() -> new CustomException(NOT_FOUND, "사용자를 찾을 수 없습니다"));

        // 채팅방 조회
        ChatRoom findChatRoom = roomRepository.findChatRoom(roomId)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "채팅방을 찾을 수 없습니다"));

        // 참가자 조회
        Boolean isParticipant = participantRepository.isParticipant(roomId, findMember.getId());

        if(!isParticipant) {
            throw new CustomException(FORBIDDEN, "해당 채팅방의 참가자가 아닙니다");
        }

        // 메세지 엔티티 저장
        ChatMessage saveChatMessage = ChatMessage.of(findChatRoom, findMember, request.getMessage());
        messageRepository.save(saveChatMessage);

        // ChatMessageResponse 객체 생성
        ChatMessageResponse response = ChatMessageResponse.builder()
                .roomId(roomId)
                .memberId(findMember.getId())
                .nickname(findMember.getNickname())
                .message(saveChatMessage.getMessage())
                .sentAt(saveChatMessage.getCreatedAt())
                .build();

        // Redis publish
        redisPublisher.publish(response);

    }

}
