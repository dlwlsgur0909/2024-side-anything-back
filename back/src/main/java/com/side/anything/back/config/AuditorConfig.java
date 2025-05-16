package com.side.anything.back.config;

import com.side.anything.back.security.jwt.TokenInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditorConfig {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if(authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }

            TokenInfo tokenInfo = (TokenInfo) authentication.getPrincipal();
            return Optional.of(tokenInfo.getId());
        };
    }

}
