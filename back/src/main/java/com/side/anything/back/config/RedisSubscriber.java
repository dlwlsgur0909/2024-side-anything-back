package com.side.anything.back.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.anything.back.chat.dto.request.ChatMessageRequest;
import com.side.anything.back.exception.BasicExceptionEnum;
import com.side.anything.back.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate; // STOMP 메세지 전송용

    // Redis에서 메세지를 수신하면 호출됨
    @Override
    public void onMessage(Message message, byte[] pattern) {

        try {
            // Redis에서 수신한 메세지(바이트)를 문자열로 변환
            String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);

            // JSON 문자열을 DTO로 역직렬화
            ChatMessageRequest request =  objectMapper.readValue(messageBody, ChatMessageRequest.class);

            // 채팅방 식별
            String destination = "/sub/chat/" + request.getRoomId();

            // STOMP 구독자에게 브로드캐스트
            messagingTemplate.convertAndSend(destination, request);

        } catch (JsonProcessingException e) {
            throw new CustomException(BasicExceptionEnum.INTERNAL_SERVER_ERROR, "메세지 저장에 실패했습니다");
        }

    }

}
