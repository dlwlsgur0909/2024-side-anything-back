package com.side.anything.back.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.anything.back.chat.dto.request.ChatMessageRequest;
import com.side.anything.back.chat.dto.response.ChatMessageResponse;
import com.side.anything.back.exception.BasicExceptionEnum;
import com.side.anything.back.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, String> redisTemplate; // RedisConfig에서 등록한 Redis에 데이터를 읽고 쓰기 위한 템플릿 객체
    private final ObjectMapper objectMapper;

    // 지정한 채널(topic)에 문자열 메세지를 전송
    public void publish(ChatMessageResponse response) {

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
            throw new CustomException(BasicExceptionEnum.INTERNAL_SERVER_ERROR, "메세지 전송에 실패했습니다");
        }
    }

}
