package com.side.anything.back.companion.dto.response;

import com.side.anything.back.companion.entity.CompanionPost;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CompanionPostDetailResponse {

    private Long id;
    private String title;
    private String content;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long memberId;
    private String nickname;
    private Boolean isClosed;
    private Boolean isApplied;

    public CompanionPostDetailResponse(CompanionPost post, Boolean isApplied) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.location = post.getLocation();
        this.startDate = post.getStartDate();
        this.endDate = post.getEndDate();
        this.memberId = post.getMember().getId();
        this.nickname = post.getMember().getNickname();
        this.isClosed = post.getStatus() != CompanionPostStatus.OPEN;
        this.isApplied = isApplied;
    }

}
