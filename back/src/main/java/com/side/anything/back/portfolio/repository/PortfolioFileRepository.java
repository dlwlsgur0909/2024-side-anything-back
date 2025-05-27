package com.side.anything.back.portfolio.repository;

import com.side.anything.back.portfolio.domain.PortfolioFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PortfolioFileRepository extends JpaRepository<PortfolioFile, Long> {

    @Query(
            """
            SELECT pf FROM PortfolioFile pf JOIN FETCH pf.portfolio p
            WHERE p.id = :portfolioId
            """
    )
    Optional<PortfolioFile> findByPortfolioId(Long portfolioId);

    Boolean existsByPortfolioId(Long portfolioId);


}
