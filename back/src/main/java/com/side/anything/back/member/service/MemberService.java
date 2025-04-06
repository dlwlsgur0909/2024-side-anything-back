package com.side.anything.back.member.service;

import com.side.anything.back.exception.CustomException;
import com.side.anything.back.jwt.TokenInfo;
import com.side.anything.back.member.domain.Member;
import com.side.anything.back.member.dto.request.MemberChangePasswordRequest;
import com.side.anything.back.member.dto.response.MemberDetailResponse;
import com.side.anything.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static com.side.anything.back.exception.BasicExceptionEnum.FORBIDDEN;
import static com.side.anything.back.exception.BasicExceptionEnum.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberDetailResponse memberDetail(final TokenInfo tokenInfo, final String username) {

        if(!tokenInfo.getUsername().equals(username)) {
            throw new CustomException(FORBIDDEN);
        }

        Member findMember = memberRepository.findByUsername(tokenInfo.getUsername())
                .orElseThrow(() -> new CustomException(NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        List<String> snsList = Arrays.asList("NAVER", "GOOGLE");
        boolean isSnsMember =  snsList.contains(findMember.getAuthentication());

        MemberDetailResponse memberDetailResponse = new MemberDetailResponse(findMember);
        memberDetailResponse.setIsSnsMember(isSnsMember);

        return memberDetailResponse;
    }

    @Transactional
    public void changePassword(final TokenInfo tokenInfo, final MemberChangePasswordRequest request) {

        String username = tokenInfo.getUsername();
        Member findMember = memberRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        if(!passwordEncoder.matches(request.getOriginalPassword(), findMember.getPassword())) {
            throw new CustomException(FORBIDDEN, "기존 비밀번호가 일치하지 않습니다");
        }

        findMember.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

}
