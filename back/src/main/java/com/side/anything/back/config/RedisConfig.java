package com.side.anything.back.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    /*
     Redis와 데이터 입출력을 처리하는 핵심 bean
     Redis에 데이터를 set, get, publish, delete 등 다양한 방식으로 다룰 수 있게 해준다
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate() {

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        /*
         String, Integer, Object 등 다양한 타입을 사용하기 때문에 RedisTemplate이 직렬화/역질렬화를 통해 데이터 형태를 Redis에 맞춰줘야 함
         지금은 Redis를 pub/sub 메세지 전송에만 사용할 예정이라 메세지도 JSON 문자열로 보내기 때문에 문자열 기반 직렬화가 가장 간단하고 효율적
         */
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }

    /*
    Redis에서 pub/sub을 사용하기 위한 수신기 컨테이너
    Redis에서 발행된 메세지를 실시간으로 구독하고 어플리케이션 내 리스너로 전달
    파라미터인 RedisSubscriber는 실제 메세지를 처리할 사용자 정의 리스너 클래스
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisSubscriber redisSubscriber) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());
        /*
        지정한 topic(채널)으로 메세지가 Redis에서 발행되면 해당 메세지를 리스너가 수신하게 됨
        PatterTopic은 채널 이름을 정확하게 지정할 수도 있고 "chat.*" 같은 패턴 매칭도 지원한다
        여기서는 단일 채팅 채널 "chatChannel"을 명시적으로 구독
        리스너를 등록하면 구독 중인 채널로 메세지가 올 때 자동으로 호출되는 방식
         */
        container.addMessageListener(redisSubscriber, new PatternTopic("chatRoom-*"));

        return container;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
