package com.side.anything.back.config;

import com.side.anything.back.security.stomp.StompJwtChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker // STOMP 기반 WebSocket 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompJwtChannelInterceptor stompJwtChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // 클라이언트가 최초 WebSocket 연결을 시도할 때 사용하는 엔드포인트
                .setAllowedOrigins("http://localhost:5173") // CORS 방지용
                .withSockJS(); // WebSocket을 지원하지 않는 환경에서는 HTTP 기반 fallback 연결을 허용 ?
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        /*
        SimpleBroker는 스프링에서 제공하는 인메모리 브로커이기 때문에 외부 브로커(RabbitMQ, Kafka)로 전환 필요
        현재는 RedisSubscriber에서 명시적으로 destination 경로에 /sub 을 포함
         */
        // registry.enableSimpleBroker("/sub"); // 서버가 클라이언트로 메세지를 보낼 때 사용하는 prefix
        registry.setApplicationDestinationPrefixes("/pub"); // 클라이언트가 서버로 메세지를 보낼 때 사용하는 prefix
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompJwtChannelInterceptor); // 메세지 수신 전 인증 처리를 위한 인터셉터 등록
    }
}
