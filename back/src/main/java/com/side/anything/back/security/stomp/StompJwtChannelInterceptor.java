package com.side.anything.back.security.stomp;

import com.side.anything.back.exception.BasicExceptionEnum;
import com.side.anything.back.exception.CustomException;
import com.side.anything.back.security.jwt.JwtUtil;
import com.side.anything.back.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class StompJwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if(StompCommand.SEND.equals(accessor.getCommand()) || StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authorization = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

            if(authorization == null || !authorization.startsWith("Bearer ")) {
                throw new CustomException(BasicExceptionEnum.UNAUTHORIZED, "Invalid Authorization");
            }

            String token = authorization.substring(7);

            if(jwtUtil.isInvalid(token)) {
                throw new CustomException(BasicExceptionEnum.UNAUTHORIZED, "Invalid JWT");
            }

            TokenInfo tokenInfo = jwtUtil.parseToken(token);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    tokenInfo, null, List.of(new SimpleGrantedAuthority(tokenInfo.getRole().name()))
            );

            accessor.setUser(authentication);

            return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
        }

        return message;
    }
}
