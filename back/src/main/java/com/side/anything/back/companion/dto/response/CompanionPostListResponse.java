package com.side.anything.back.companion.dto.response;

import com.side.anything.back.companion.entity.CompanionPost;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class CompanionPostListResponse {

    private List<CompanionPostResponse> companionPostList;
    private Integer totalPages;

    public CompanionPostListResponse(List<CompanionPost> companionPostList, Integer totalPages) {
        this.companionPostList = companionPostList.stream()
                .map(CompanionPostResponse::new)
                .toList();
        this.totalPages = totalPages;
    }

    @Getter
    private static class CompanionPostResponse {
        private Long id;
        private String title;
        private String location;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;

        public CompanionPostResponse(CompanionPost companionPost) {
            this.id = companionPost.getId();
            this.title = companionPost.getTitle();
            this.location = companionPost.getLocation();
            this.startDate = companionPost.getStartDate();
            this.endDate = companionPost.getEndDate();
            this.status = companionPost.getStatus().getDescription();
        }

    }
}
