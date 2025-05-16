package com.side.anything.back.portfolio.domain;

import com.side.anything.back.base.BaseEntity;
import com.side.anything.back.member.domain.Member;
import com.side.anything.back.portfolio.dto.request.PortfolioSaveRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Portfolio extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_id")
    private Long id;

    @Column(name = "portfolio_name")
    private String name;

    @Column(name = "portfolio_content")
    private String content;

    @Column(name = "portfolio_url")
    private String url;

    @Column(name = "is_public")
    private Boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static Portfolio of(PortfolioSaveRequest request, Member member) {

        Portfolio portfolio = new Portfolio();

        portfolio.name = request.getPortfolioName();
        portfolio.content = request.getPortfolioContent();
        portfolio.url = request.getPortfolioUrl();
        portfolio.isPublic = request.getIsPublic();
        portfolio.member = member;

        return portfolio;
    }

    public void update(PortfolioSaveRequest request) {
        this.name = request.getPortfolioName();
        this.content = request.getPortfolioContent();
        this.url = request.getPortfolioUrl();
        this.isPublic = request.getIsPublic();
    }

}
