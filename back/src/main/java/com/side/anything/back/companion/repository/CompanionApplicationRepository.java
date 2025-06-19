package com.side.anything.back.companion.repository;

import com.side.anything.back.companion.entity.CompanionApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanionApplicationRepository extends JpaRepository<CompanionApplication, Long> {
    Boolean existsByMemberIdAndCompanionPostId(Long memberId, Long companionPostId);
}
