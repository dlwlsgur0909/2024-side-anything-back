package com.side.anything.back.member.repository;

import com.side.anything.back.member.domain.Member;
import com.side.anything.back.member.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdminRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m where m.role = :role")
    List<Member> findMemberList(@Param("role") Role role);

}
