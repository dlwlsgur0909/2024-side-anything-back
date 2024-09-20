package com.side.anything.back.security;

import com.side.anything.back.member.domain.Member;
import com.side.anything.back.member.domain.Role;
import com.side.anything.back.member.repository.MemberRepository;
import com.side.anything.back.security.dto.response.*;
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
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }else{
            return null;
        }
        
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        CustomUserDTO userDTO = CustomUserDTO.builder()
                .username(username)
                .name(oAuth2Response.getName())
                .email(oAuth2Response.getEmail())
                .role(Role.ROLE_USER)
                .build();

        Member findMember = memberRepository.findByUsername(username)
                .orElse(null);

        // 최초 로그인 시도
        if(findMember == null) {

            // 이메일 중복 확인
            Boolean isDuplicated = memberRepository.existsByEmail(userDTO.getEmail());

            if(isDuplicated) {
                throw new OAuth2AuthenticationException(new OAuth2Error("403"), "403");
            }

            Member member = Member.builder()
                    .username(userDTO.getUsername())
                    .password("")
                    .name(userDTO.getName())
                    .email(userDTO.getEmail())
                    .role(userDTO.getRole())
                    .authentication(registrationId.toUpperCase())
                    .verified(true)
                    .build();

            memberRepository.save(member);
        }else {
            findMember.updateOAuth2(userDTO);
        }

        return new CustomOAuth2User(userDTO);
    }
}
