package com.side.anything.back.security.oauth2;

import com.side.anything.back.member.domain.Member;
import com.side.anything.back.member.domain.Role;
import com.side.anything.back.member.repository.MemberRepository;
import com.side.anything.back.security.oauth2.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException { // OAuth2UserRequest -> 리소스 서버에서 제공되는 유저 정보

        // DefaultOAuth2UserService 생성자 호출
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 어떤 서비스에서 온 요청인지 확인하는 값 (naver, google)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 서비스 응답에 대한 공통 인터페이스 각 DTO는 OAuth2Response를 구현한다
        OAuth2Response oAuth2Response = null;

        // 서비스 마다 응답 객체 구조가 다르기 때문에 서비스에 맞는 DTO를 사용
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }else{
            return null;
        }

        // 리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        // CustomOAuth2User에 넘겨줄 DTO 생성
        CustomUserDTO userDTO = CustomUserDTO.builder()
                .username(username)
                .name(oAuth2Response.getName())
                .email(oAuth2Response.getEmail())
                .role(Role.USER)
                .build();

        // username을 기준으로 회원 데이터 조회
        Member findMember = memberRepository.findByUsername(username)
                .orElse(null);

        // 최초 로그인 시도
        if(findMember == null) {

            // 이메일 중복 확인
            Boolean isDuplicated = memberRepository.existsByEmail(userDTO.getEmail());

            if(isDuplicated) {
                throw new OAuth2AuthenticationException(new OAuth2Error("403"), "403");
            }

            // 최초 로그인 시 DB에 저장할 Member 엔티티 생성
            Member member = Member.builder()
                    .username(userDTO.getUsername())
                    .password("")
                    .name(userDTO.getName())
                    .email(userDTO.getEmail())
                    .role(userDTO.getRole())
                    .authentication(registrationId.toUpperCase())
                    .verified(true)
                    .build();

            Member savedMember = memberRepository.save(member);
            userDTO.setId(savedMember.getId());
        }else {
            // 이미 가입된 회원인 경우 갱신할 유저 정보 업데이트
            findMember.updateOAuth2(userDTO);
            userDTO.setId(findMember.getId());
        }

        return new CustomOAuth2User(userDTO);
    }
}
