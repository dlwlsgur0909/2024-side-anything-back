package com.side.anything.back.config;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, String> redisTemplate;

    // 지정한 채널(topic)에 문자열 메세지를 전송
    public void publish(String topic, String messageJson) {
        // messageJson은 DTO를 JSON으로 직렬화한 문자열
        redisTemplate.convertAndSend(topic, messageJson);
    }

}
