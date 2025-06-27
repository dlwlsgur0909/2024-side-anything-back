package com.side.anything.back.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.anything.back.chat.dto.request.ChatMessageRequest;
import com.side.anything.back.chat.entity.ChatParticipant;
import com.side.anything.back.chat.entity.ChatRoom;
import com.side.anything.back.chat.repository.ChatMessageRepository;
import com.side.anything.back.chat.repository.ChatParticipantRepository;
import com.side.anything.back.chat.repository.ChatRoomRepository;
import com.side.anything.back.config.RedisPublisher;
import com.side.anything.back.exception.BasicExceptionEnum;
import com.side.anything.back.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.side.anything.back.exception.BasicExceptionEnum.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository roomRepository;
    private final ChatParticipantRepository participantRepository;
    private final ChatMessageRepository messageRepository;

    private final RedisPublisher redisPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public void sendMessage(ChatMessageRequest request) {

        // 채팅방 존재 여부 확인
        Boolean exists = roomRepository.existsByIdAndIsActiveTrue(request.getRoomId());

        if(!exists) {
            throw new CustomException(NOT_FOUND, "채팅방을 찾을 수 없습니다");
        }

        // 참가자 여부 확인
        Boolean isParticipant = participantRepository.isParticipant(request.getRoomId(), request.getMemberId());

        if(!isParticipant) {
            throw new CustomException(FORBIDDEN, "해당 채팅방의 참가자가 아닙니다");
        }

        try {
            // Request 문자열로 직렬화
            String messageJson = objectMapper.writeValueAsString(request);
            // Redis에 topic(채널명)으로 메세지 발행(publish)
            String topic = "여기에 topic prefix" + request.getRoomId();
            redisPublisher.publish(topic, messageJson);
        } catch (JsonProcessingException e) {
            throw new CustomException(INTERNAL_SERVER_ERROR, "메세지 전송에 실패했습니다");
        }


    }




}
