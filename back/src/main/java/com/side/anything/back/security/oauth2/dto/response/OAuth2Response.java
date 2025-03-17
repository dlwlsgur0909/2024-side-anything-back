package com.side.anything.back.security.oauth2.dto.response;

public interface OAuth2Response {

    // 제공자 (EX: naver, google ...)
    String getProvider();

    // 제공자가 발급해주는 아이디(번호)
    String getProviderId();

    // 이메일
    String getEmail();

    // 사용자 실명 (설정한 이름)
    String getName();
}
