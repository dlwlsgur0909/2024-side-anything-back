package com.side.anything.back.member.repository;

import com.side.anything.back.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByUsernameAndEmail(String username, String email);

    Optional<Member> findByIdAndVerifiedTrue(Long memberId);
}
