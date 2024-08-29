package com.side.anything.back.member.service;

import com.side.anything.back.jwt.JwtUtil;
import com.side.anything.back.member.domain.Member;
import com.side.anything.back.member.dto.request.MemberJoinRequest;
import com.side.anything.back.member.dto.request.MemberLoginRequest;
import com.side.anything.back.member.dto.response.MemberLoginResponse;
import com.side.anything.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public Member join(final MemberJoinRequest request) {

        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new NoSuchElementException();
        }

        String rawPassword = request.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        Member member = request.toEntity(encodedPassword);

        return memberRepository.save(member);
    }

    public MemberLoginResponse login(final MemberLoginRequest request) {

        Member findMember = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(NoSuchElementException::new);

        boolean isMatch = passwordEncoder.matches(request.getPassword(), findMember.getPassword());

        if(!isMatch) {
            throw new NoSuchElementException("ID/Password Does Not Match");
        }

        String accessToken = jwtUtil.createAccessToken(findMember);
        String refreshToken = jwtUtil.createRefreshToken(findMember);

        return MemberLoginResponse.builder()
                .username(findMember.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
