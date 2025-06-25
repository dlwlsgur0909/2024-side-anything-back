package com.side.anything.back.companion.repository;

import com.side.anything.back.companion.entity.CompanionApplication;
import com.side.anything.back.companion.entity.CompanionApplicationStatus;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanionApplicationRepository extends JpaRepository<CompanionApplication, Long> {

    @Query(
            """
            SELECT COUNT(ca) > 0 FROM CompanionApplication ca
            WHERE
                ca.member.id = :memberId
                AND ca.companionPost.id = :companionPostId
                AND ca.status IN :statusList
            """
    )
    Boolean isApplied(Long memberId, Long companionPostId,
                      List<CompanionApplicationStatus> statusList);

    @Modifying
    @Query(
            """
            UPDATE CompanionApplication ca
            SET
                status = :status
            WHERE
                ca.companionPost.id = :companionPostId
                AND ca.status IN :statusList
            """
    )
    void cancelByHost(@Param("companionPostId") Long companionPostId,
                      @Param("status") CompanionApplicationStatus status,
                      @Param("statusList")List<CompanionApplicationStatus> statusList);

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

    @Query(
            """
            SELECT ca FROM CompanionApplication ca
            JOIN FETCH ca.companionPost cp
            WHERE
                ca.id = :companionApplicationId
                AND ca.member.id = :memberId
            """
    )
    Optional<CompanionApplication> findMyApplication(@Param("companionApplicationId") Long companionApplicationId,
                                                     @Param("memberId") Long memberId);

    @Query(
            """
            SELECT ca FROM CompanionApplication ca
            JOIN FETCH ca.member m
            WHERE
                ca.companionPost.id = :companionPostId
                AND ca.status IN :statusList
            ORDER BY
                ca.id DESC
            """
    )
    List<CompanionApplication> findApplicationListByPost(@Param("companionPostId") Long companionPostId,
                                                         @Param("statusList") List<CompanionApplicationStatus> statusList);


    @Query(
            """
            SELECT ca FROM CompanionApplication ca
            WHERE
                ca.companionPost.id = :companionPostId
                AND ca.companionPost.status = :postStatus
                AND ca.companionPost.member.id = :memberId
                AND ca.id = :companionApplicationId
                AND ca.status = :applicationStatus
            """
    )
    Optional<CompanionApplication> findApplicationByPost(@Param("companionPostId") Long companionPostId,
                                                         @Param("postStatus") CompanionPostStatus postStatus,
                                                         @Param("memberId") Long memberId,
                                                         @Param("companionApplicationId") Long companionApplicationId,
                                                         @Param("applicationStatus") CompanionApplicationStatus applicationStatus);

}
