package com.side.anything.back.portfolio.repository;

import com.side.anything.back.portfolio.domain.Portfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    @Query(
            """
            SELECT
                p
            FROM
                Portfolio p
            JOIN FETCH
                p.member
            WHERE
                p.id = :id
            """
    )
    Optional<Portfolio> findById(@Param("id") Long id);

    Page<Portfolio> findAllByMemberId(@Param("id") Long id, Pageable pageable);
}
