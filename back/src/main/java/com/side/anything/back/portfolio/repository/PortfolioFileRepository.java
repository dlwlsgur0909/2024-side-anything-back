package com.side.anything.back.portfolio.repository;

import com.side.anything.back.portfolio.domain.PortfolioFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioFileRepository extends JpaRepository<PortfolioFile, Long> {
}
