package com.side.anything.back.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.anything.back.chat.dto.request.ChatMessageRequest;
import com.side.anything.back.exception.BasicExceptionEnum;
import com.side.anything.back.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    // 지정한 채널(topic)에 문자열 메세지를 전송
    public void publish(ChatMessageRequest request) {

        try {
            // JSON 문자열로 직렬화
            String messageJson = objectMapper.writeValueAsString(request);
            // 채널명
            String topic = "chatRoom." + request.getRoomId();

            // 직렬화한 문자열을 publish
            redisTemplate.convertAndSend(topic, messageJson);

        } catch (JsonProcessingException e) {
            throw new CustomException(BasicExceptionEnum.INTERNAL_SERVER_ERROR, "메세지 전송에 실패했습니다");
        }
    }

}
