package com.side.anything.back.portfolio.domain;

import com.side.anything.back.util.dto.response.FileInfo;
import com.side.anything.back.util.file.BaseFileEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioFile extends BaseFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_file_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    public static PortfolioFile of(FileInfo fileInfo, Portfolio portfolio) {
        PortfolioFile portfolioFile = new PortfolioFile();
        portfolioFile.setFileInfo(fileInfo);
        portfolioFile.portfolio = portfolio;

        return portfolioFile;
    }
}
