package com.side.anything.back.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.anything.back.chat.dto.request.ChatMessageRequest;
import com.side.anything.back.chat.entity.ChatMessage;
import com.side.anything.back.chat.entity.ChatRoom;
import com.side.anything.back.chat.repository.ChatMessageRepository;
import com.side.anything.back.chat.repository.ChatRoomRepository;
import com.side.anything.back.exception.BasicExceptionEnum;
import com.side.anything.back.exception.CustomException;
import com.side.anything.back.member.entity.Member;
import com.side.anything.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final ChatRoomRepository roomRepository;
    private final ChatMessageRepository messageRepository;
    private final MemberRepository memberRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        try {
            // Redis에서 수신한 메세지 객체를 문자열로 변환
            String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
            // JSON 문자열을 DTO로 변환
            ChatMessageRequest request =  objectMapper.readValue(messageBody, ChatMessageRequest.class);

            // 채팅방 조회
            ChatRoom findChatRoom = roomRepository.findChatRoom(request.getRoomId())
                    .orElseThrow(() -> new CustomException(BasicExceptionEnum.NOT_FOUND, "채팅방을 찾을 수 없습니다"));


            // 회원 조회 로직 수정 (isVerified, isProfileCompleted 등 기타 검증 값 필요)
            // 회원 조회
            Member findMember = memberRepository.findById(request.getMemberId())
                    .orElseThrow(() -> new CustomException(BasicExceptionEnum.NOT_FOUND, "회원을 찾을 수 없습니다"));

            // 메세지 생성 및 저장
            messageRepository.save(ChatMessage.of(findChatRoom, findMember, request.getMessage()));
        } catch (JsonProcessingException e) {
            throw new CustomException(BasicExceptionEnum.INTERNAL_SERVER_ERROR, "메세지 저장에 실패했습니다");
        }

    }

}
