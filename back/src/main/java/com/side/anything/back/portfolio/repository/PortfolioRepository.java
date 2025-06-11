package com.side.anything.back.portfolio.repository;

import com.side.anything.back.portfolio.domain.Portfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    @NonNull
    @Query(
            """
            SELECT
                p
            FROM
                Portfolio p
            JOIN FETCH
                p.member m
            WHERE
                p.id = :id
            """
    )
    Optional<Portfolio> findById(@NonNull @Param("id") Long id);

    @Query(
            value = "SELECT p FROM Portfolio p JOIN p.member m WHERE m.id = :memberId AND p.name LIKE %:keyword%",
            countQuery = "SELECT COUNT(p) FROM Portfolio p JOIN p.member m WHERE m.id = :memberId AND p.name LIKE %:keyword%"
    )
    Page<Portfolio> findMyPortfolioList(@Param("memberId") Long memberId,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);

    @Query(
            value = """
                    SELECT p FROM Portfolio p JOIN FETCH p.member m
                    WHERE (p.isPublic = true or m.id = :memberId)
                    AND (p.name LIKE %:keyword% OR m.name LIKE %:keyword%)
                    """,
            countQuery = """
                        SELECT COUNT(p) FROM Portfolio p JOIN p.member m
                        WHERE (p.isPublic = true OR m.id = :memberId)
                        AND (p.name LIKE %:keyword% OR m.name LIKE %:keyword%)
                        """
    )
    Page<Portfolio> findPortfolioList(@Param("memberId") Long memberId,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);

    @Modifying
    @Query("DELETE FROM Portfolio p WHERE p.id = :id")
    void deleteById(@NonNull @Param("id") Long id);

}
