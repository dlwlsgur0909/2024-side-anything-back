package com.side.anything.back.companion.dto.response;

import com.side.anything.back.companion.entity.CompanionPost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class CompanionPostListResponse {

    private List<CompanionPostResponse> companionPostList = new ArrayList<>();
    private Integer totalPages;

    @Getter
    public static class CompanionPostResponse {
        private Long id;
        private String title;
        private String location;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer recruitCount;
        private Boolean isClosed;

        public CompanionPostResponse(CompanionPost companionPost) {
            this.id = companionPost.getId();
            this.title = companionPost.getTitle();
            this.location = companionPost.getLocation();
            this.startDate = companionPost.getStartDate();
            this.endDate = companionPost.getEndDate();
            this.recruitCount = companionPost.getRecruitCount();
            this.isClosed = companionPost.getIsClosed();
        }

    }
}
