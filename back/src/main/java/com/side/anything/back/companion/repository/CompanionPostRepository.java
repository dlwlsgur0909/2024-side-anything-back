package com.side.anything.back.companion.repository;

import com.side.anything.back.companion.entity.CompanionPost;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanionPostRepository extends JpaRepository<CompanionPost, Long> {

    @Query(
            value = """
                    SELECT cp FROM CompanionPost cp
                    WHERE
                        (
                            cp.title LIKE %:keyword%
                            OR cp.location LIKE  %:keyword%
                        )
                        AND cp.status != :status
                    ORDER BY
                        cp.id DESC
                    """,
            countQuery = """
                        SELECT COUNT(cp) FROM CompanionPost cp
                        WHERE
                            (
                                cp.title LIKE %:keyword%
                                OR cp.location LIKE  %:keyword%
                            )
                            AND cp.status != :status
                        """
    )
    Page<CompanionPost> findPostList(@Param("keyword") String keyword,
                                     @Param("status") CompanionPostStatus status,
                                     Pageable pageable);

    @Query("SELECT cp FROM CompanionPost cp JOIN FETCH cp.member m WHERE cp.id = :id AND cp.status != :status")
    Optional<CompanionPost> findPostDetail(@Param("id") Long id, @Param("status") CompanionPostStatus status);

    @Query(
            value = """
                    SELECT cp FROM CompanionPost cp
                    WHERE
                        (
                            cp.title LIKE %:keyword%
                            OR cp.location LIKE %:keyword%
                        )
                        AND cp.status != :status
                        AND cp.member.id = :memberId
                    ORDER BY
                        cp.id DESC
                    """,
            countQuery = """
                        SELECT COUNT(cp) FROM CompanionPost cp
                        WHERE
                            (
                                cp.title LIKE %:keyword%
                                OR cp.location LIKE %:keyword%
                            )
                            AND cp.status != :status
                            AND cp.member.id = :memberId
                        """
    )
    Page<CompanionPost> findMyPostList(@Param("keyword") String keyword,
                                       @Param("status") CompanionPostStatus status,
                                       @Param("memberId") Long memberId,
                                       Pageable pageable);

    @Query(
            """
            SELECT cp FROM CompanionPost cp
            WHERE
                cp.id = :companionPostId
                AND cp.member.id = :memberId
                AND cp.status != :status
            """
    )
    Optional<CompanionPost> findMyPostDetail(@Param("companionPostId") Long companionPostId,
                                             @Param("memberId") Long memberId,
                                             @Param("status") CompanionPostStatus status);
}
