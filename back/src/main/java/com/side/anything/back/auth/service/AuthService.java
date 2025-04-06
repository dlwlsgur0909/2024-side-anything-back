package com.side.anything.back.auth.service;

import com.side.anything.back.auth.dto.request.*;
import com.side.anything.back.auth.dto.response.MemberLoginResponse;
import com.side.anything.back.exception.CustomException;
import com.side.anything.back.jwt.JwtUtil;
import com.side.anything.back.jwt.TokenInfo;
import com.side.anything.back.member.domain.Member;
import com.side.anything.back.member.domain.Role;
import com.side.anything.back.member.repository.MemberRepository;
import com.side.anything.back.util.EmailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Objects;

import static com.side.anything.back.exception.BasicExceptionEnum.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    // 중복 아이디 검증
    public Boolean isUniqueUsername(final MemberDuplicateCheckRequest request) {

        return !memberRepository.existsByUsername(request.getUsernameOrEmail());
    }

    // 중복 이메일 검증
    public Boolean isUniqueEmail(final MemberDuplicateCheckRequest request) {

        return !memberRepository.existsByEmail(request.getUsernameOrEmail());
    }

    // 회원가입
    @Transactional
    public void join(final MemberJoinRequest request) {

        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(CONFLICT, "이미 사용중인 아이디 입니다");
        }

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(CONFLICT, "이미 사용중인 이메일 입니다");
        }
        String rawPassword = request.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 인증번호 메일 발송
        String authentication = emailService.sendJoinMail(request.getEmail());

        Member member = request.toEntity(encodedPassword, authentication);
        memberRepository.save(member);
    }

    // 회원가입 인증
    @Transactional
    public void verify(final MemberVerifyRequest request) {

        String username = request.getUsername();
        Member findMember = memberRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        if(findMember.getVerified()) {
            throw new CustomException(CONFLICT, "이미 인증된 회원입니다");
        }

        if(!findMember.getAuthentication().equals(request.getAuthentication())) {
            throw new CustomException(UNAUTHORIZED, "인증번호가 일치하지 않습니다");
        }

        findMember.verify();
    }

    // 인증메일 재전송
    @Transactional
    public void sendEmail(final MemberDuplicateCheckRequest request) {

        String username = request.getUsernameOrEmail();

        Member findMember = memberRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        // 인증번호 메일 발송
        String authentication = emailService.sendJoinMail(findMember.getEmail());
        findMember.updateAuthentication(authentication);
    }

    // 로그인
    public MemberLoginResponse login(final HttpServletResponse response, final MemberLoginRequest request) {

        Member findMember = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(UNAUTHORIZED, "가입되지 않은 회원입니다"));

        boolean isMatch = passwordEncoder.matches(request.getPassword(), findMember.getPassword());

        if(!isMatch) {
            throw new CustomException(UNAUTHORIZED, "아이디/비밀번호를 확인해주세요");
        }

        if(!findMember.getVerified()) {
            throw new CustomException(UNAUTHORIZED, "미인증 회원입니다. 인증 후 로그인해주세요");
        }

        String accessToken = jwtUtil.createAccessToken(new TokenInfo(findMember));
        String refreshToken = jwtUtil.createRefreshToken(new TokenInfo(findMember));

        response.addCookie(createCookie("Refresh", refreshToken));

        return MemberLoginResponse.builder()
                .username(findMember.getUsername())
                .name(findMember.getName())
                .role(findMember.getRole())
                .accessToken(accessToken)
                .build();
    }

    // 아이디 찾기
    public String findUsername(final MemberFindUsernameRequest request) {

        String email = request.getEmail();
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "미가입 회원입니다"));

        String authentication = findMember.getAuthentication();

        if(Arrays.asList("NAVER", "GOOGLE").contains(authentication)) {
            throw new CustomException(FORBIDDEN, authentication + "로 가입된 회원입니다");
        }

        return findMember.getUsername();
    }

    // 비밀번호 찾기
    @Transactional
    public void findPassword(final MemberFindPasswordRequest request) {

        String username = request.getUsername();
        String email = request.getEmail();

        Member findMember = memberRepository.findByUsernameAndEmail(username, email)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "일치하는 회원을 찾을 수 없습니다"));

        String authentication = findMember.getAuthentication();

        if(Arrays.asList("NAVER", "GOOGLE").contains(authentication)) {
            throw new CustomException(FORBIDDEN, authentication + "로 가입된 회원입니다");
        }

        // 비밀번호 초기화 메일 발송
        String randomNumber = emailService.sendResetPasswordMail(email);
        findMember.updatePassword(passwordEncoder.encode(randomNumber));
    }

    // 토큰 재발급 (새로고침, AccessToken 만료 시)
    public MemberLoginResponse reissue(final HttpServletResponse response, final HttpServletRequest request) {

        String refreshToken = null;

        Cookie[] cookies = request.getCookies();

        Cookie tempCookie = new Cookie("Refresh", null);
        tempCookie.setMaxAge(0);
        response.addCookie(tempCookie);

        if(cookies == null) {
            log.error("Reissue Error - Empty Cookie");
            throw new CustomException(UNAUTHORIZED, "로그인이 만료되었습니다");
        }

        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("Refresh")) {
                refreshToken = cookie.getValue();
            }
        }

        if(refreshToken == null || jwtUtil.isInvalid(refreshToken)) {
            log.error("Reissue Error - Invalid Refresh Token -> {}", refreshToken);
            throw new CustomException(UNAUTHORIZED, "로그인이 만료되었습니다");
        }

        if(!jwtUtil.checkRefreshToken(refreshToken)) {
            log.error("Reissue Error - Refresh Token Does Not Exist in Redis");
            throw new CustomException(UNAUTHORIZED, "로그인이 만료되었습니다");
        }

        jwtUtil.deleteRefreshToken(refreshToken);

        TokenInfo tokenInfo = jwtUtil.parseToken(refreshToken);

        String name = tokenInfo.getName();
        String username = tokenInfo.getUsername();
        Role role = tokenInfo.getRole();

        String newAccessToken = jwtUtil.createAccessToken(tokenInfo);
        String newRefreshToken = jwtUtil.createRefreshToken(tokenInfo);
        response.addCookie(createCookie("Refresh", newRefreshToken));

        return MemberLoginResponse.builder()
                .username(username)
                .name(name)
                .role(role)
                .accessToken(newAccessToken)
                .build();
    }

    // 소셜 로그인
    public MemberLoginResponse socialLoginSuccess(final HttpServletResponse response, final HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if(cookies == null) {
            log.error("Social Login Failed - Cookie is Empty");
            throw new CustomException(UNAUTHORIZED, "로그인이 만료되었습니다");
        }

        String accessToken = null;

        for (Cookie cookie : cookies) {
            // Access 쿠키 삭제
            if(cookie.getName().equals("Access")) {
                accessToken = cookie.getValue();
                cookie.setPath("/auth/login-success");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        if(Objects.isNull(accessToken)) {
            log.error("Social Login Failed: Invalid Access Token - {}", accessToken);
            throw new CustomException(UNAUTHORIZED, "로그인이 만료되었습니다");
        }

        TokenInfo tokenInfo = jwtUtil.parseToken(accessToken);

        String username = tokenInfo.getUsername();
        String name = tokenInfo.getName();
        Role role = tokenInfo.getRole();

        String refreshToken = jwtUtil.createRefreshToken(tokenInfo);
        response.addCookie(createCookie("Refresh", refreshToken));

        return MemberLoginResponse.builder()
                .username(username)
                .name(name)
                .role(role)
                .accessToken(accessToken)
                .build();
    }

    // 로그아웃
    public void logout(final HttpServletRequest request, final HttpServletResponse response) {

        String refresh = null;
        Cookie[] cookies = request.getCookies();

        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("Refresh")) {
                    refresh = cookie.getValue();
                }
            }

            if(refresh != null) {
                jwtUtil.deleteRefreshToken(refresh);
            }
        }

        Cookie cookie = new Cookie("Refresh", null);
        cookie.setPath("/auth/reissue");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }

    // 쿠키 생성
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60);
        cookie.setPath("/auth/reissue");
        cookie.setHttpOnly(true);

        return cookie;
    }

}
