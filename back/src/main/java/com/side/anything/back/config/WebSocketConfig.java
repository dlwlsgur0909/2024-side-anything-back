package com.side.anything.back.config;

import com.side.anything.back.exception.CustomException;
import com.side.anything.back.security.stomp.StompJwtChannelInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompEncoder;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

@Slf4j
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

    /*
    Spring은 기본적으로 interceptor 예외를 처리하지 않는다
    따라서, StompJwtChannelInterceptor에서 CustomException이 발생했을 때
    클라이언트는 이유를 모른 채 연결만 끊긴다
    WebSocket 연결 흐름에서 이 예외를 감지하고, STOMP ERROR 프레임을 클라이언트로 보내는 처리를 위해
    데코레이터를 등록하면 클라이언트는 onStompError 콜백에서 errorMessage를 받아서 처리할 수 있다
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {

        /*
         WebSocketTransportRegistration은 WebSocket 세션의 연결과 종료를 처리하는 내부 핸들러들을 설정할 수 있다
         addDecoratorFactory()는 이런 기본 핸들러를 감싸는 데코레이터를 추가해줄 수 있음
         */
        registry.addDecoratorFactory(handler -> new WebSocketHandlerDecorator(handler) {

            // WebSocket 연결 성공 직후 (STOMP CONNECT 완료 직후) 실행
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {

                try {
                    /*
                    클라이언트가 WebSocket을 통해 엔드포인트(/ws)에 연결을 시도할 때 호출되는 부분
                    연결 성공 처리를 위임하고 이때 StompJwtChannelInterceptor가 실행되어 토큰을 검증하는데
                    interceptor에서 에러가 발생하면 아래 catch 구문에서 처리할 수 있다
                     */
                    super.afterConnectionEstablished(session);
                }catch (CustomException ce) {
                    // CustomException 처리
                    sendStompErrorFrame(session, ce.getErrorMessage()); // 클라이언트에 에러 프레임 전송
                    session.close(); // 세션 종료
                }catch (Exception e) {
                    // CustomException 외 예기치 못한 에러 처리
                    log.error("Unexpected error during Websocket Connection", e);
                    sendStompErrorFrame(session, "서버 오류입니다");
                    session.close();
                }
            }

            /*
            WebSocket 세션 객체를 받아서 STOMP ERROR 프레임을 직접 생성하여 클라이언트로 전송
            Spring은 기본적으로 이 오류 프레임을 자동으로 만들어서 내려주지 않아 직접 구현해야 한다
             */
            private void sendStompErrorFrame(WebSocketSession session, String errorMessage) {

                try {
                    // STOMP 헤더 만들기 (ERROR COMMAND)
                    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
                    // 클라이언트에게 전송할 에러 메세지로 에러 프레임의 헤더에 "message"라는 키값으로 들어간다
                    accessor.setMessage(errorMessage);
                    /*
                    Spring 내부의 MessageBuilder나 StompEncoder는 immutable한 헤더에 값을 설정할 수 없음
                    따라서 인코딩을 위해 mutable 상태로 변경해야 한다
                     */
                    accessor.setLeaveMutable(true);

                    // STOMP 메세지 객체 생성 (에러 프레임은 보통 payload 없음)
                    Message<byte[]> errorMessageFrame = MessageBuilder
                            .withPayload(new byte[0])
                            .setHeaders(accessor)
                            .build();

                    // STOMP 메세지 객체를 바이트 배열로 직렬화
                    byte[] serialized = new StompEncoder().encode(errorMessageFrame);

                    // WebSocket 세션을 통해 클라이언트에게 전송
                    session.sendMessage(new TextMessage(serialized));

                }catch (Exception e) {
                    log.error("STOMP Error Frame 전송 실패", e);
                }
            }
        });
    }
}
