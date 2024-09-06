package com.side.anything.back.member.repository;

import com.side.anything.back.member.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void changePasswordTest() {

        // given
        String username = "member";

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(NoSuchElementException::new);

        String resetPassword = "1234";

        member.updatePassword(passwordEncoder.encode(resetPassword));
        memberRepository.save(member);
        // when

        Member findMember = memberRepository.findByUsername(username)
                .orElseThrow(NoSuchElementException::new);

        // then
        Assertions.assertThat(passwordEncoder.matches(resetPassword, findMember.getPassword())).isTrue();

    }

}