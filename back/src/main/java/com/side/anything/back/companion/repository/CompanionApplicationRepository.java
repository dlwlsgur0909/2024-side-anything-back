package com.side.anything.back.companion.repository;

import com.side.anything.back.companion.entity.CompanionApplication;
import com.side.anything.back.companion.entity.CompanionApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanionApplicationRepository extends JpaRepository<CompanionApplication, Long> {
    Boolean existsByMemberIdAndCompanionPostId(Long memberId, Long companionPostId);

    @Modifying
    @Query(
            """
            UPDATE CompanionApplication ca
            SET
                status = :status
            WHERE
                ca.companionPost.id = :companionPostId
                AND ca.status IN :targetStatusList
            """
    )
    void cancelByHost(@Param("companionPostId") Long companionPostId,
                      @Param("status") CompanionApplicationStatus status,
                      @Param("targetStatusList")List<CompanionApplicationStatus> targetStatusList);

    @Query(
            value = """
                    SELECT ca FROM CompanionApplication ca
                    JOIN FETCH ca.companionPost cp
                    WHERE
                        ca.member.id = :memberId
                        AND ca.status != :status
                    ORDER BY
                        ca.id DESC
                    """,
            countQuery = """
                        SELECT COUNT(ca) FROM CompanionApplication ca
                        WHERE
                            ca.member.id = :memberId
                            AND ca.status != :status
                        """
    )
    Page<CompanionApplication> findMyApplicationList(@Param("memberId") Long memberId,
                                                     @Param("status") CompanionApplicationStatus status,
                                                     Pageable pageable);

}
