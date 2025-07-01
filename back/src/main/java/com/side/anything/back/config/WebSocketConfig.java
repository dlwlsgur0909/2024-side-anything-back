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
@EnableWebSocketMessageBroker // STOMP 기반 WebSocket 활성화
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompJwtChannelInterceptor stompJwtChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat") // 클라이언트가 최초 WebSocket 연결을 시도할 때 사용하는 엔드포인트
                .setAllowedOrigins("http://localhost:5173") // CORS 방지용
                .withSockJS(); // WebSocket을 지원하지 않는 브라우저에서도 fallback으로 지원 ?
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub"); // 서버가 클라이언트로 메세지를 보낼 때 사용하는 prefix
        registry.setApplicationDestinationPrefixes("/pub"); // 클라이언트가 서버로 메세지를 보낼 때 사용하는 prefix
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompJwtChannelInterceptor);
    }
}
