package com.side.anything.back.portfolio.repository;

import com.side.anything.back.portfolio.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    List<Portfolio> findAllByMemberId(Long id);
}
