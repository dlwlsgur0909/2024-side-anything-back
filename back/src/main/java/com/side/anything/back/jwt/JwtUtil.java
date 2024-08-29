package com.side.anything.back.jwt;

import com.side.anything.back.member.domain.Member;
import com.side.anything.back.member.domain.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key secretKey;
    @Value("${spring.jwt.access-timeout")
    private final Long accessTimeout;
    @Value("${spring.jwt.refresh-timeout")
    private final Long refreshTimeout;


    public JwtUtil(@Value("${spring.jwt.secret}") String secret,
                   @Value("${spring.jwt.access-timeout") Long accessTimeout,
                   @Value("${spring.jwt.refresh-timeout") Long refreshTimeout) {

        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTimeout = accessTimeout;
        this.refreshTimeout = refreshTimeout;
    }

    public String createJwt(Member member, Long expiration) {

        return Jwts.builder()
                .claim("id", member.getId())
                .claim("username", member.getUsername())
                .claim("role", member.getRole())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    public String createAccessToken(Member member) {

        return createJwt(member, accessTimeout * 60 * 1000L);
    }

    public String createRefreshToken(Member member) {

        return createJwt(member, refreshTimeout * 60 * 1000L);
    }


    public Long getId(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJwt(token)
                .getBody()
                .get("id", Long.class);
    }

    public String getUsername(String token) {

        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJwt(token)
                .getBody()
                .get("username", String.class);
    }

    public Role getRole(String token) {

        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJwt(token)
                .getBody()
                .get("role", Role.class);
    }

    public Boolean isExpired(String token) {

        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJwt(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }


}
