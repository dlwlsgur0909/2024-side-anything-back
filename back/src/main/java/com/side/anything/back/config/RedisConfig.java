package com.side.anything.back.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    private final RedisSubscriber redisSubscriber;

    /*
    Redis 서버와 연결하는 ConnectionFactory 빈
    LettuceConnectionFactory를 사용하여 Redis 호스트와 포트로 접속 환경 구성
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    /*
     데이터 입출력을 처리하는 핵심 bean
     Redis에 데이터를 set, get, publish, delete 등 다양한 방식으로 다룰 수 있게 해준다
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate() {

        RedisTemplate<String, String> template = new RedisTemplate<>(); // Redis에 데이터를 읽고 쓰기 위한 템플릿 객체
        template.setConnectionFactory(redisConnectionFactory()); // Redis 서버와 연결된 팩토리를 주입 받는다
        /*
         setKeySerializer, setValueSerializer 메서드는 Redis에 데이터를 저장할 때 사용하는 직렬화 방식을 지정
         String, Integer, Object 등 다양한 타입을 사용하기 때문에 RedisTemplate이 직렬화/역직렬화를 통해 데이터 형태를 Redis에 맞춰줘야 함
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
    public RedisMessageListenerContainer redisMessageListenerContainer() {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer(); // Redis Pub/Sub 구독을 관리하는 컴포넌트
        container.setConnectionFactory(redisConnectionFactory()); // Redis 서버와 연결된 팩토리를 주입 받는다
        /*
        지정한 topic(채널)으로 메세지가 Redis에서 발행되면 해당 메세지를 리스너가 수신하게 됨
        PatternTopic은 채널 이름을 정확하게 지정할 수도 있고 "chat.*" 같은 패턴 매칭도 지원한다
        여기서는 단일 채팅 채널 "chatRoom"을 명시적으로 구독 -> RedisPublisher가 메세지를 발행(publish)할 때 특정 채널(여기서는 chatRoom. + roomId)을 지정함
        필요하면 여러 채널과 여러 messageListener를 등록할 수 있음
        리스너를 등록하면 구독 중인 채널로 메세지가 올 때 자동으로 호출되는 방식
         */
        container.addMessageListener(redisSubscriber, new PatternTopic("chatRoom.*"));

        return container;
    }

    /*
    Redis가 메세지를 수신했을 때 호출할 메서드를 지정하는 어댑터
    여기서는 RedisSubscriber의 onMessage 메서드를 메세지 수신 시 호출하도록 지정
     */
    @Bean
    public MessageListenerAdapter messageListenerAdapter() {
        return new MessageListenerAdapter(redisSubscriber, "onMessage");
    }

}
