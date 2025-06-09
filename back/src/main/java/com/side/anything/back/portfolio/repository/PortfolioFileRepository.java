package com.side.anything.back.portfolio.repository;

import com.side.anything.back.portfolio.domain.PortfolioFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface PortfolioFileRepository extends JpaRepository<PortfolioFile, Long> {

    @Query("SELECT pf FROM PortfolioFile pf where pf.portfolio.id = :portfolioId")
    Optional<PortfolioFile> findByPortfolioId(@Param("portfolioId") Long portfolioId);

    @Modifying
    @Query("DELETE FROM PortfolioFile pf WHERE pf.portfolio.id = :portfolioId")
    void deleteByPortfolioId(@Param("portfolioId") Long portfolioId);

    @Modifying
    @Query("DELETE FROM PortfolioFile pf WHERE pf.id = :portfolioFileId")
    void deleteById(@NonNull @Param("portfolioFileId") Long portfolioFileId);
}
