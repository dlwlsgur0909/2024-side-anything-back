package com.side.anything.back.member.service;

import com.side.anything.back.exception.BasicCustomException;
import com.side.anything.back.exception.BasicExceptionEntity;
import com.side.anything.back.jwt.JwtUtil;
import com.side.anything.back.jwt.TokenInfo;
import com.side.anything.back.member.domain.Member;
import com.side.anything.back.member.dto.request.MemberDuplicateCheckRequest;
import com.side.anything.back.member.dto.request.MemberJoinRequest;
import com.side.anything.back.member.dto.request.MemberLoginRequest;
import com.side.anything.back.member.dto.request.MemberVerifyRequest;
import com.side.anything.back.member.dto.response.MemberLoginResponse;
import com.side.anything.back.member.repository.MemberRepository;
import com.side.anything.back.util.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;


    public Boolean isUniqueUsername(final MemberDuplicateCheckRequest request) {

        return !memberRepository.existsByUsername(request.getUsernameOrEmail());
    }

    public Boolean isUniqueEmail(final MemberDuplicateCheckRequest request) {

        return !memberRepository.existsByEmail(request.getUsernameOrEmail());
    }

    @Transactional
    public void join(final MemberJoinRequest request) {

        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new BasicCustomException(HttpStatus.FORBIDDEN, "403", "이미 사용중인 아이디 입니다");
        }

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new BasicCustomException(HttpStatus.FORBIDDEN, "403", "이미 사용중인 이메일 입니다");
        }
        String rawPassword = request.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        String authentication = emailService.sendMail(request.getEmail());

        Member member = request.toEntity(encodedPassword, authentication);
        memberRepository.save(member);
    }

    @Transactional
    public void resendEmail(final MemberDuplicateCheckRequest request) {

        String username = request.getUsernameOrEmail();

        Member findMember = memberRepository.findByUsername(username)
                .orElseThrow(() -> new BasicCustomException(HttpStatus.NOT_FOUND, "404", "회원 정보를 찾을 수 없습니다"));

        String authentication = emailService.sendMail(findMember.getEmail());
        findMember.updateAuthentication(authentication);
    }

    @Transactional
    public void verify(final MemberVerifyRequest request) {

        String username = request.getUsername();
        Member findMember = memberRepository.findByUsername(username)
                .orElseThrow(() -> new BasicCustomException(HttpStatus.NOT_FOUND, "404", "회원 정보를 찾을 수 없습니다"));

        if(findMember.getVerified()) {
            throw new BasicCustomException(HttpStatus.FORBIDDEN, "403", "이미 인증된 회원입니다");
        }

        if(!findMember.getAuthentication().equals(request.getAuthentication())) {
            throw new BasicCustomException(HttpStatus.FORBIDDEN, "403", "인증번호가 일치하지 않습니다");
        }

        findMember.verify();
    }

    public MemberLoginResponse login(final MemberLoginRequest request) {

        Member findMember = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(NoSuchElementException::new);

        boolean isMatch = passwordEncoder.matches(request.getPassword(), findMember.getPassword());

        if(!isMatch) {
            throw new NoSuchElementException("ID/Password Does Not Match");
        }

        String accessToken = jwtUtil.createAccessToken(new TokenInfo(findMember));
        String refreshToken = jwtUtil.createRefreshToken(new TokenInfo(findMember));

        return MemberLoginResponse.builder()
                .username(findMember.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
