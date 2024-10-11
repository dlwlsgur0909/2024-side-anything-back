package com.side.anything.back.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.anything.back.util.RedisService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Objects;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Long accessTimeout;
    private final Long refreshTimeout;
    private final RedisService redisService;


    public JwtUtil(@Value("${spring.jwt.secret}") String secret,
                   @Value("${spring.jwt.access-timeout}") Long accessTimeout,
                   @Value("${spring.jwt.refresh-timeout}") Long refreshTimeout,
                   RedisService redisService) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessTimeout = accessTimeout;
        this.refreshTimeout = refreshTimeout;
        this.redisService = redisService;
    }

    private String createJwt(TokenInfo tokenInfo, Long expiration) {

        return Jwts.builder()
                .claim("token", tokenInfo)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    public String createAccessToken(TokenInfo tokenInfo) {

        return createJwt(tokenInfo, accessTimeout * 60 * 1000L);
    }

    public String createRefreshToken(TokenInfo tokenInfo) {

        String refreshToken = createJwt(tokenInfo, refreshTimeout * 60 * 1000L);
        redisService.setValue(refreshToken, refreshToken, Duration.ofMinutes(refreshTimeout));
        return refreshToken;
    }

    // 토큰 검증
    public boolean isInvalid(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return false;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return true;
    }

    // 토큰 정보 추출
    public TokenInfo parseToken(String token) {

        LinkedHashMap tokenInfo = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("token", LinkedHashMap.class);

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.convertValue(tokenInfo, TokenInfo.class);
    }

    public Boolean checkRefreshToken(String key) {
        return Objects.nonNull(redisService.getValue(key));
    }

    public void deleteRefreshToken(String key) {
        redisService.deleteValue(key);
    }

}
