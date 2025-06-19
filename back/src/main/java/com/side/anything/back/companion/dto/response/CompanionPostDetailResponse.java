package com.side.anything.back.companion.dto.response;

import com.side.anything.back.companion.entity.CompanionPost;
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
    private String writer;
    private Boolean isClosed;

    public CompanionPostDetailResponse(CompanionPost companionPost) {
        this.id = companionPost.getId();
        this.title = companionPost.getTitle();
        this.content = companionPost.getContent();
        this.location = companionPost.getLocation();
        this.startDate = companionPost.getStartDate();
        this.endDate = companionPost.getEndDate();
        this.memberId = companionPost.getMember().getId();
        this.writer = companionPost.getMember().getNickname();
        this.isClosed = companionPost.getIsClosed();
    }

}
