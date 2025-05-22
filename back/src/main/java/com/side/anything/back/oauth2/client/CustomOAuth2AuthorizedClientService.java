package com.side.anything.back.oauth2.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public class CustomOAuth2AuthorizedClientService {
    /*
    OAuth2 소셜 로그인을 진행하는 사용자에 대해 서버는 외부 인증 서버에서 발급 받은 Access 토큰과 같은 정보를 담을 저장소가 필요하다
    기본적으로 인메모리 방식으로 관리되는데 소셜 로그인 사용자가 증가하고 서버의 스케일 아웃 문제로 인해 인메모리 방식은 실무에서 사용되지 않는다
    따라서, DB에 해당 정보를 저장하기 위해서는 OAuth2AuthorizedClientService를 직접 작성해야 한다
    JPA를 사용하려면 커스텀으로 작업해야 할 코드가 많기 때문에 기본적으로 지원하는 JDBC를 사용한다 (gradle 의존성 주입 필요)
    DB 접속 정보는 yml의 동일한 값을 사용한다
    DB에는 정보를 저장할 테이블이 필요하다

        CREATE TABLE oauth2_authorized_client (
          client_registration_id varchar(100) NOT NULL,
          principal_name varchar(200) NOT NULL,
          access_token_type varchar(100) NOT NULL,
          access_token_value blob NOT NULL,
          access_token_issued_at timestamp NOT NULL,
          access_token_expires_at timestamp NOT NULL,
          access_token_scopes varchar(1000) DEFAULT NULL,
          refresh_token_value blob DEFAULT NULL,
          refresh_token_issued_at timestamp DEFAULT NULL,
          created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
          PRIMARY KEY (client_registration_id, principal_name)
        );

        client_registration_id와 principal_name이 중복되면 문제가 발생할 수 있다...
        예를 들어 같은 닉네임 (principal_name)을 가진 사용자가 둘 다 Naver를 이용해서 동시에 로그인을 한다면?
        스프링 시큐리티 공식 답변에 의하면 OAuth2AuthorizedClientService를 직접 구현해서 사용하라는 답변이 있다
     */

    @Bean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService(JdbcTemplate jdbcTemplate,
                                                                       ClientRegistrationRepository clientRegistrationRepository) {
        // CustomClientRegistrationRepository를 사용한다
        return new JdbcOAuth2AuthorizedClientService(jdbcTemplate, clientRegistrationRepository);
    }
}
