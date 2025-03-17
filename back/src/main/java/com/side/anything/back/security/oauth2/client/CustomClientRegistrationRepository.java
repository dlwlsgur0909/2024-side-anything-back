package com.side.anything.back.security.oauth2.client;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

@Configuration
@RequiredArgsConstructor
public class CustomClientRegistrationRepository {

    // SocialClientRegistration을 주입 받는다
    private final SocialClientRegistration socialClientRegistration;

    // 주입 받은 SocialClientRegistration을 사용해서 ClientRegistrationRepository를 반환한다
    // 저장 방식은 InMemory 방식, JDBC를 활용한 DB 저장 방식이 있다
    // 정보가 많지 않기 때문에 InMemory를 사용해도 무방하다
    public ClientRegistrationRepository clientRegistrationRepository() {

        return new InMemoryClientRegistrationRepository(
                socialClientRegistration.naverClientRegistration(),
                socialClientRegistration.googleClientRegistration()
        );
    }

}
