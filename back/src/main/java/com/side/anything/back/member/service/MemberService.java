package com.side.anything.back.member.service;

import com.side.anything.back.exception.BasicCustomException;
import com.side.anything.back.jwt.TokenInfo;
import com.side.anything.back.member.domain.Member;
import com.side.anything.back.member.dto.response.MemberDetailResponse;
import com.side.anything.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberDetailResponse memberDetail(final TokenInfo tokenInfo, final String username) {

        if(!tokenInfo.getUsername().equals(username)) {
            throw new BasicCustomException(HttpStatus.FORBIDDEN, "403", "잘못된 접근입니다");
        }

        Member findMember = memberRepository.findByUsername(tokenInfo.getUsername())
                .orElseThrow(() -> new BasicCustomException(HttpStatus.NOT_FOUND, "404", "회원 정보를 찾을 수 없습니다"));

        return new MemberDetailResponse(findMember);
    }

}
