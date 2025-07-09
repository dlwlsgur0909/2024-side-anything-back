package com.side.anything.back.member.repository;

import com.side.anything.back.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    Optional<Member> findByUsernameAndIsVerifiedTrue(String username);

    Boolean existsByUsername(String username);

    Boolean existsByNickname(String nickname);

    Boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByUsernameAndEmail(String username, String email);

    @Query(
            """
            SELECT m FROM Member m
            WHERE
                m.id = :memberId
                AND m.isVerified = true
                AND m.isProfileCompleted = true
            """
    )
    Optional<Member> findMemberById(Long memberId);
}
