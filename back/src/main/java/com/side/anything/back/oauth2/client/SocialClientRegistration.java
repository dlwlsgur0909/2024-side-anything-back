package com.side.anything.back.oauth2.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.stereotype.Component;

@Component
public class SocialClientRegistration { // yml 파일에 외부 서버에 대한 정보를 설정하지 않고 각 서비스에 맞는 ClientRegistration을 등록
    private final String naverClientId;
    private final String naverClientSecret;
    private final String googleClientId;
    private final String googleClientSecret;

    public SocialClientRegistration(@Value("${social.naver.client.id}") String naverClientId,
                                    @Value("${social.naver.client.secret}") String naverClientSecret,
                                    @Value("${social.google.client.id}") String googleClientId,
                                    @Value("${social.google.client.secret}") String googleClientSecret) {

        this.naverClientId = naverClientId;
        this.naverClientSecret = naverClientSecret;
        this.googleClientId = googleClientId;
        this.googleClientSecret = googleClientSecret;

    }

    public ClientRegistration naverClientRegistration() {

        return ClientRegistration.withRegistrationId("naver")
                .clientId(naverClientId)
                .clientSecret(naverClientSecret)
                .redirectUri("http://localhost:8090/login/oauth2/code/naver")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("name", "email")
                .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                .tokenUri("https://nid.naver.com/oauth2.0/token")
                .userInfoUri("https://openapi.naver.com/v1/nid/me")
                .userNameAttributeName("response")
                .build();
    }

    public ClientRegistration googleClientRegistration() {

        return ClientRegistration.withRegistrationId("google")
                .clientId(googleClientId)
                .clientSecret(googleClientSecret)
                .redirectUri("http://localhost:8090/login/oauth2/code/google")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("profile", "email")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .issuerUri("https://accounts.google.com")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .build();
    }
}
