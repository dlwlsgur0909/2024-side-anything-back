package com.side.anything.back.security.stomp;

import com.side.anything.back.security.jwt.JwtUtil;
import com.side.anything.back.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

/*
메세지 채널 인터셉터로 WebSocket 메세지를 처리하기 전에 가로챈다
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StompJwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    // 메세지가 실제 전송되기 직전에 호출됨
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        /*
        STOMP 명령어(CONNECT, SUBSCRIBE, SEND 등) 가능
        Native Header 접근(Authorization 등) 가능
         */
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message); // STOMP 관련 정보(명령어 헤더 등)를 추출할 수 있도록 래핑

        // STOMP 명령어가 CONNECT, SEND 인 경우에 JWT 인증 수행
        if(StompCommand.SEND.equals(accessor.getCommand()) || StompCommand.CONNECT.equals(accessor.getCommand())) {

            // STOMP 메세지는 일반 HTTP와 달리 Native header 형태로 데이터를 주고 받는다)
            String authorization = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

            if(authorization == null || !authorization.startsWith("Bearer ")) {
                log.error("WebSocket Invalid Authorization - {}", authorization);
                throw new MessagingException("UNAUTHORIZED");
            }

            String token = authorization.substring(7);

            if(jwtUtil.isInvalid(token)) {
                log.error("WebSocket Invalid JWT - {}", token);
                throw new MessagingException("UNAUTHORIZED");
            }

            TokenInfo tokenInfo = jwtUtil.parseToken(token);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    tokenInfo, null, List.of(new SimpleGrantedAuthority(tokenInfo.getRole().name()))
            );

            /*
            STOMP 메세지의 헤더(simpUser 키값)에 인증된 사용자 정보(Principal)를 설정
            이 인증 정보는 Spring Security가 내부적으로 사용하는 SecurityContext에는 들어가지 않는다
            하지만 STOMP 세션 및 메세지 핸들링 사 Principal로 자동 연결된다
            이후 WebSocket 핸들러ㅔ서 메세지를 수신할 때 Principal 객체를 통해 사용자 식별이 가능하다
             */
            accessor.setUser(authentication);

            /*
             STOMP 메세지는 불변 객체이고 StompHeaderAccessor는 wrapper이기 때문에 setUser 같은 변경은 원본에 직접 반영되지 않는다
             따라서 인증 정보를 헤더에 설정했기 때문에 메세지를 새롭게 만들어서 반환해야 한다
             기존 메세지의 payload는 유지하고 인증이 반영된 headers로 메세지를 재구성한다
             */
            return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
        }

        return message;
    }
}
