package com.side.anything.back.auth.service;

import com.side.anything.back.auth.dto.request.*;
import com.side.anything.back.auth.dto.response.LoginResponse;
import com.side.anything.back.exception.CustomException;
import com.side.anything.back.security.jwt.JwtUtil;
import com.side.anything.back.security.jwt.TokenInfo;
import com.side.anything.back.member.entity.Member;
import com.side.anything.back.member.entity.Role;
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
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    // 회원가입
    @Transactional
    public void join(final JoinRequest request) {

        // 중복 검증 로직 시작
        if (!isUniqueUsername(request.getUsername())) {
            throw new CustomException(CONFLICT, "이미 사용중인 아이디 입니다");
        }

        if (!isUniqueNickname(request.getNickname())) {
            throw new CustomException(CONFLICT, "이미 사용중인 닉네임 입니다");
        }

        if (!isUniqueEmail(request.getEmail())) {
            throw new CustomException(CONFLICT, "이미 사용중인 이메일 입니다");
        }
        // 중복 검증 로직 종료

        String rawPassword = request.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 인증번호 메일 발송
        String authentication = emailService.sendJoinMail(request.getEmail());

        Member member = Member.of(request, encodedPassword, authentication);

        memberRepository.save(member);
    }

    // 회원가입 인증
    @Transactional
    public void verify(final VerifyRequest request) {

        String username = request.getUsername();
        Member findMember = findMemberByUsername(username);

        if(findMember.getIsVerified()) {
            throw new CustomException(CONFLICT, "이미 인증된 회원입니다");
        }

        if(!findMember.getAuthentication().equals(request.getAuthentication())) {
            throw new CustomException(UNAUTHORIZED, "인증번호가 일치하지 않습니다");
        }

        findMember.verify();
    }

    // 인증메일 재전송
    @Transactional
    public void sendEmail(final EmailRequest request) {

        Member findMember = findMemberByUsername(request.getUsername());

        // 인증번호 메일 발송
        String authentication = emailService.sendJoinMail(findMember.getEmail());
        findMember.updateAuthentication(authentication);
    }

    // 로그인
    public LoginResponse login(final HttpServletResponse response, final LoginRequest request) {

        Member findMember = findMemberByUsername(request.getUsername());

        boolean isMatch = passwordEncoder.matches(request.getPassword(), findMember.getPassword());

        if(!isMatch) {
            throw new CustomException(UNAUTHORIZED, "아이디/비밀번호를 확인해주세요");
        }

        if(!findMember.getIsVerified()) {
            throw new CustomException(FORBIDDEN, "미인증 회원입니다. 인증 후 로그인해주세요");
        }

        String accessToken = jwtUtil.createAccessToken(new TokenInfo(findMember));
        String refreshToken = jwtUtil.createRefreshToken(new TokenInfo(findMember));

        response.addCookie(createCookie("Refresh", refreshToken, "/auth/reissue"));

        return LoginResponse.builder()
                .id(findMember.getId())
                .nickname(findMember.getNickname())
                .name(findMember.getName())
                .role(findMember.getRole())
                .isProfileCompleted(findMember.getIsProfileCompleted())
                .accessToken(accessToken)
                .build();
    }

    // 아이디 찾기
    public String findUsername(final FindUsernameRequest request) {

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
    public void findPassword(final FindPasswordRequest request) {

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
    public LoginResponse reissue(final HttpServletResponse response, final HttpServletRequest request) {

        String refreshToken = null;

        Cookie[] cookies = request.getCookies();

        Cookie tempCookie = new Cookie("Refresh", null);
        tempCookie.setPath("/auth/reissue");
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

//        if(!jwtUtil.checkRefreshToken(refreshToken)) {
//            log.error("Reissue Error - Refresh Token Does Not Exist in Redis");
//            throw new CustomException(UNAUTHORIZED, "로그인이 만료되었습니다");
//        }
//
//        jwtUtil.deleteRefreshToken(refreshToken);

        TokenInfo tokenInfo = jwtUtil.parseToken(refreshToken);

        Long id = tokenInfo.getId();
        String name = tokenInfo.getName();
        String nickname = tokenInfo.getNickname();
        Role role = tokenInfo.getRole();
        Boolean isProfileCompleted = tokenInfo.getIsProfileCompleted();

        String newAccessToken = jwtUtil.createAccessToken(tokenInfo);
        String newRefreshToken = jwtUtil.createRefreshToken(tokenInfo);
        response.addCookie(createCookie("Refresh", newRefreshToken, "/auth/reissue"));

        return LoginResponse.builder()
                .id(id)
                .nickname(nickname)
                .name(name)
                .role(role)
                .isProfileCompleted(isProfileCompleted)
                .accessToken(newAccessToken)
                .build();
    }

    // 소셜 회원가입
    @Transactional
    public LoginResponse socialJoin(final HttpServletResponse response, final HttpServletRequest request,
                                    final SocialJoinRequest socialJoinRequest) {

        String refreshToken = null;

        Cookie[] cookies = request.getCookies();

        Cookie tempCookie = new Cookie("Refresh", null);
        tempCookie.setPath("/auth/social-join");
        tempCookie.setMaxAge(0);
        response.addCookie(tempCookie);

        if(cookies == null) {
            log.error("Social Join Error - Empty Cookie");
            throw new CustomException(UNAUTHORIZED, "로그인이 만료되었습니다");
        }

        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("Refresh")) {
                refreshToken = cookie.getValue();
            }
        }

        if(refreshToken == null || jwtUtil.isInvalid(refreshToken)) {
            log.error("Social Join Error - Invalid Refresh Token -> {}", refreshToken);
            throw new CustomException(UNAUTHORIZED, "로그인이 만료되었습니다");
        }

//        if(!jwtUtil.checkRefreshToken(refreshToken)) {
//            log.error("Social Join Error - Refresh Token Does Not Exist in Redis");
//            throw new CustomException(UNAUTHORIZED, "로그인이 만료되었습니다");
//        }
//
//        jwtUtil.deleteRefreshToken(refreshToken);

        Member findMember = memberRepository.findById(socialJoinRequest.getId())
                .orElseThrow(() -> new CustomException(UNAUTHORIZED, "미가입 회원입니다"));

        if(findMember.getIsProfileCompleted()) {
            throw new CustomException(FORBIDDEN, "이미 저장된 추가 정보가 있습니다");
        }

        if(!isUniqueNickname(socialJoinRequest.getNickname())) {
            throw new CustomException(CONFLICT, "이미 사용중인 닉네임 입니다");
        }

        findMember.socialJoin(socialJoinRequest);

        String newAccessToken = jwtUtil.createAccessToken(new TokenInfo(findMember));
        String newRefreshToken = jwtUtil.createRefreshToken(new TokenInfo(findMember));

        response.addCookie(createCookie("Refresh", newRefreshToken, "/auth/reissue"));

        return LoginResponse.builder()
                .id(findMember.getId())
                .nickname(findMember.getNickname())
                .name(findMember.getName())
                .role(findMember.getRole())
                .isProfileCompleted(findMember.getIsProfileCompleted())
                .accessToken(newAccessToken)
                .build();
    }

    // 소셜 로그인
    public LoginResponse socialLoginSuccess(final HttpServletResponse response, final HttpServletRequest request) {

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

        if(Objects.isNull(accessToken) || jwtUtil.isInvalid(accessToken)) {
            log.error("Social Login Failed: Invalid Access Token - {}", accessToken);
            throw new CustomException(UNAUTHORIZED, "로그인이 만료되었습니다");
        }

        TokenInfo tokenInfo = jwtUtil.parseToken(accessToken);

        Long id = tokenInfo.getId();
        String nickname = tokenInfo.getNickname();
        String name = tokenInfo.getName();
        Role role = tokenInfo.getRole();
        Boolean isProfileCompleted = tokenInfo.getIsProfileCompleted();

        String refreshToken = jwtUtil.createRefreshToken(tokenInfo);

        String cookiePath = isProfileCompleted ? "/auth/reissue" : "/auth/social-join";
        response.addCookie(createCookie("Refresh", refreshToken, cookiePath));

        return LoginResponse.builder()
                .id(id)
                .nickname(nickname)
                .name(name)
                .role(role)
                .isProfileCompleted(isProfileCompleted)
                .accessToken(accessToken)
                .build();
    }

    // 로그아웃
    public void logout(final HttpServletRequest request, final HttpServletResponse response) {

//        String refresh = null;
//        Cookie[] cookies = request.getCookies();
//
//        if(cookies != null) {
//            for (Cookie cookie : cookies) {
//                if(cookie.getName().equals("Refresh")) {
//                    refresh = cookie.getValue();
//                    break;
//                }
//            }
//
//            if(refresh != null) {
//                jwtUtil.deleteRefreshToken(refresh);
//            }
//        }

        Cookie cookie = new Cookie("Refresh", null);
        cookie.setPath("/auth/reissue");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    /* 내부 메서드 */

    // 중복 아이디 검증
    private Boolean isUniqueUsername(final String username) {
        return !memberRepository.existsByUsername(username);
    }

    // 중복 닉네임 검증
    private Boolean isUniqueNickname(final String nickname) {
        return !memberRepository.existsByNickname(nickname);
    }

    // 중복 이메일 검증
    private Boolean isUniqueEmail(final String email) {

        return !memberRepository.existsByEmail(email);
    }

    // 미인증 회원 조회 (아이디 대소문자 구분)
    private Member findMemberByUsername(String username) {

        Member findMember = memberRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "가입되지 않은 회원입니다"));

        if(!findMember.getUsername().equals(username)) {
            throw new CustomException(NOT_FOUND, "가입되지 않은 회원입니다");
        }

        return findMember;
    }

    // 쿠키 생성
    private Cookie createCookie(String key, String value, String cookiePath) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60);
        cookie.setPath(cookiePath);
        cookie.setHttpOnly(true);

        return cookie;
    }

}
