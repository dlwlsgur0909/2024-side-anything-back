package com.side.anything.back.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.anything.back.chat.dto.request.ChatMessageRequest;
import com.side.anything.back.chat.dto.response.ChatMessageResponse;
import com.side.anything.back.exception.BasicExceptionEnum;
import com.side.anything.back.exception.BasicExceptionResponse;
import com.side.anything.back.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate; // STOMP 프로토콜 기반으로 WebSocket 클라이언트에게 메세지를 전송

    // Redis Pub/Sub을 통해 들어온 메세지를 받아서 처리하는 콜백 메서드
    @Override
    public void onMessage(Message message, byte[] pattern) {

        ChatMessageResponse response = null;

        try {
            /*
            message.getBody()로 Redis에서 수신한 메세지의 바이트 배열을 가져옴
            new String()을 통해 바이트 배열을 UTF-8 인코딩된 문자열로 변환
             */
            String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);

            // JSON 문자열을 DTO로 역직렬화
            response =  objectMapper.readValue(messageBody, ChatMessageResponse.class);

            // 채팅방 식별
            String destination = "/sub/chat/" + response.getRoomId();

            // STOMP 구독자에게 브로드캐스트
            messagingTemplate.convertAndSend(destination, response);

        } catch (JsonProcessingException e) {
            if(response == null) {
                // 시스템 에러 처리?
            }else {
                handleSubscribeException(response.getRoomId(), response.getMemberId());
            }
        } catch (Exception e) {
            if(response == null) {
                // 시스템 에러 처리?
            }else {
                handleSubscribeException(response.getRoomId(), response.getMemberId());
            }
        }
    }

    private void handleSubscribeException(Long roomId, Long memberId) {
        String errorMessage = "메세지 전송에 실패했습니다";
        String destination = "/sub/chat/" + roomId + "/errors";
        messagingTemplate.convertAndSend(destination, new ErrorPayload(memberId, errorMessage));
    }

    @Getter
    private static class ErrorPayload {
        private Long memberId;
        private String errorMessage;

        public ErrorPayload(Long memberId, String errorMessage) {
            this.memberId = memberId;
            this.errorMessage = errorMessage;
        }
    }
}
