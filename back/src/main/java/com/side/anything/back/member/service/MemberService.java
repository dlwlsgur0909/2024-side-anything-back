package com.side.anything.back.member.service;

import com.side.anything.back.member.domain.Member;
import com.side.anything.back.member.dto.request.MemberJoinRequest;
import com.side.anything.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberService {


    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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

}
