package com.side.anything.back.auth.service;

import com.side.anything.back.auth.dto.request.*;
import com.side.anything.back.exception.BasicCustomException;
import com.side.anything.back.jwt.JwtUtil;
import com.side.anything.back.jwt.TokenInfo;
import com.side.anything.back.member.domain.Member;
import com.side.anything.back.auth.dto.response.MemberLoginResponse;
import com.side.anything.back.member.repository.MemberRepository;
import com.side.anything.back.util.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

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

        String authentication = emailService.sendJoinMail(request.getEmail());

        Member member = request.toEntity(encodedPassword, authentication);
        memberRepository.save(member);
    }

    @Transactional
    public void sendEmail(final MemberDuplicateCheckRequest request) {

        String username = request.getUsernameOrEmail();

        Member findMember = memberRepository.findByUsername(username)
                .orElseThrow(() -> new BasicCustomException(HttpStatus.NOT_FOUND, "404", "회원 정보를 찾을 수 없습니다"));

        String authentication = emailService.sendJoinMail(findMember.getEmail());
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
                .orElseThrow(() -> new BasicCustomException(HttpStatus.UNAUTHORIZED, "401", "가입되지 않은 회원입니다"));

        boolean isMatch = passwordEncoder.matches(request.getPassword(), findMember.getPassword());

        if(!isMatch) {
            throw new BasicCustomException(HttpStatus.UNAUTHORIZED, "401", "아이디/비밀번호를 확인해주세요");
        }

        if(!findMember.getVerified()) {
            throw new BasicCustomException(HttpStatus.FORBIDDEN, "403", "미인증 회원입니다. 인증 후 로그인해주세요");
        }

        String accessToken = jwtUtil.createAccessToken(new TokenInfo(findMember));
        String refreshToken = jwtUtil.createRefreshToken(new TokenInfo(findMember));

        return MemberLoginResponse.builder()
                .username(findMember.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String findUsername(final MemberFindUsernameRequest request) {

        String email = request.getEmail();
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BasicCustomException(HttpStatus.NOT_FOUND, "404", "미가입 회원입니다"));

        return findMember.getUsername();
    }

    @Transactional
    public void findPassword(final MemberFindPasswordRequest request) {

        String username = request.getUsername();
        String email = request.getEmail();

        Member findMember = memberRepository.findByUsernameAndEmail(username, email)
                .orElseThrow(() -> new BasicCustomException(HttpStatus.NOT_FOUND, "404", "일치하는 회원을 찾을 수 없습니다"));

        String randomNumber = emailService.sendResetPasswordMail(email);
        findMember.updatePassword(passwordEncoder.encode(randomNumber));
    }

    public MemberLoginResponse reissue(final ReissueRequest request) {

        String refreshToken = request.getRefreshToken();

        if(jwtUtil.isInvalid(refreshToken)) {
            throw new BasicCustomException(HttpStatus.UNAUTHORIZED, "401", "로그인이 만료되었습니다");
        }

        TokenInfo tokenInfo = jwtUtil.parseToken(refreshToken);
        String username = tokenInfo.getUsername();
        String newAccessToken = jwtUtil.createAccessToken(tokenInfo);
        String newRefreshToken = jwtUtil.createRefreshToken(tokenInfo);

        return MemberLoginResponse.builder()
                .username(username)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }


}
