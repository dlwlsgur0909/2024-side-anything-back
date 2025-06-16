package com.side.anything.back.member.repository;

import com.side.anything.back.member.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void saveMember() {
//        Member member = Member.builder()
//                .username("myTest")
//                .password("testPassword")
//                .name("테스트 회원")
//                .role(Role.USER)
//                .email("myTest@test.com")
//                .authentication("TEST")
//                .isVerified(true)
//                .build();
//
//        memberRepository.save(member);
    }

    @Test
    void changePasswordTest() {

        // given
        String username = "myTest";

        Member member = memberRepository.findByUsernameAndIsVerifiedTrue(username)
                .orElseThrow(NoSuchElementException::new);

        String resetPassword = "1234";

        member.updatePassword(passwordEncoder.encode(resetPassword));
        memberRepository.save(member);
        // when

        Member findMember = memberRepository.findByUsernameAndIsVerifiedTrue(username)
                .orElseThrow(NoSuchElementException::new);

        // then
        Assertions.assertThat(passwordEncoder.matches(resetPassword, findMember.getPassword())).isTrue();

    }

}