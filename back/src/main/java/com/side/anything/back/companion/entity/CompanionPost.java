package com.side.anything.back.companion.entity;

import com.side.anything.back.base.BaseEntity;
import com.side.anything.back.companion.dto.request.CompanionPostSaveRequest;
import com.side.anything.back.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanionPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "location")
    private String location;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CompanionPostStatus status;

    public static CompanionPost of(CompanionPostSaveRequest request, Member member) {

        CompanionPost companionPost = new CompanionPost();
        companionPost.title = request.getTitle();
        companionPost.content = request.getContent();
        companionPost.location = request.getLocation();
        companionPost.startDate = request.getStartDate();
        companionPost.endDate = request.getEndDate();
        companionPost.member = member;
        companionPost.status = CompanionPostStatus.OPEN;

        return companionPost;
    }

}
