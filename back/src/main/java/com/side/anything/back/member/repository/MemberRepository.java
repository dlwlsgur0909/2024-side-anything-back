package com.side.anything.back.member.repository;

import com.side.anything.back.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    Optional<Member> findByUsernameAndIsVerifiedTrue(String username);

    Boolean existsByUsername(String username);

    Boolean existsByNickname(String nickname);

    Boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByUsernameAndEmail(String username, String email);

    Optional<Member> findByIdAndIsVerifiedTrue(Long memberId);
}
