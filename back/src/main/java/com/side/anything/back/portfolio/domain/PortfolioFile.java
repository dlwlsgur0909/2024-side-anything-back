package com.side.anything.back.portfolio.domain;

import com.side.anything.back.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_file_id")
    private Long id;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "stored_filename")
    private String storedFilename;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    public static PortfolioFile of(String originalFilename, String storedFilename, Portfolio portfolio) {
        PortfolioFile portfolioFile = new PortfolioFile();
        portfolioFile.originalFilename = originalFilename;
        portfolioFile.storedFilename = storedFilename;
        portfolioFile.portfolio = portfolio;

        return portfolioFile;
    }
}
