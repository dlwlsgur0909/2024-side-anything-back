package com.side.anything.back.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.anything.back.chat.dto.response.ChatExceptionResponse;
import com.side.anything.back.chat.dto.response.ChatMessageResponse;
import com.side.anything.back.exception.BasicExceptionEnum;
import com.side.anything.back.exception.CustomException;
import io.jsonwebtoken.io.SerializationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, String> redisTemplate; // RedisConfig에서 등록한 Redis에 데이터를 읽고 쓰기 위한 템플릿 객체
    private final SimpMessageSendingOperations messagingTemplate; // STOMP 프로토콜 기반으로 WebSocket 클라이언트에게 메세지를 전송

    private final ObjectMapper objectMapper;

    // 지정한 채널(topic)에 문자열 메세지를 전송
    public void publish(ChatMessageResponse response) {

        // 트랜잭션 커밋 이후에 메세지를 전송
        if(TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        // JSON 문자열로 직렬화
                        String messageJson = objectMapper.writeValueAsString(response);

                        /*
                        RedisConfig의 RedisMessageListenerContainer 빈에 등록한
                        messageListener의 PatternTopic(chatRoom.*)에 맞는 채널명
                         */
                        String topic = "chatRoom." + response.getRoomId(); // 채널명

                        /*
                        직렬화한 문자열을 publish
                        지정한 채널(topic)에 메세지를 발행해서 구독 중인 리스너에게 전달
                         */
                        redisTemplate.convertAndSend(topic, messageJson);

                    } catch (JsonProcessingException e) {
                        log.error("Redis Publish Failed: Cannot covert response to json", e);
                        handlePublishException(response.getRoomId(), response.getMemberId());
                    } catch (RedisConnectionFailureException e) {
                        log.error("Redis Publish Failed: Cannot connect to redis server", e);
                        handlePublishException(response.getRoomId(), response.getMemberId());
                    } catch (SerializationException e) {
                        log.error("Redis Publish Failed: Cannot serialized message", e);
                        handlePublishException(response.getRoomId(), response.getMemberId());
                    } catch (Exception e) {
                        log.error("Redis Publish Failed: Unexpected error occurred", e);
                        handlePublishException(response.getRoomId(), response.getMemberId());
                    }
                }
            });
        }else {
            log.error("Missing Transaction while redis publish");
            handlePublishException(response.getRoomId(), response.getMemberId());
        }
    }

    private void handlePublishException(Long roomId, Long memberId) {
        ChatExceptionResponse chatExceptionResponse = new ChatExceptionResponse(memberId, 500, "메세지 전송에 실패했습니다");
        String destination = "/sub/chat/" + roomId + "/errors";
        messagingTemplate.convertAndSend(destination, chatExceptionResponse);
    }

}
