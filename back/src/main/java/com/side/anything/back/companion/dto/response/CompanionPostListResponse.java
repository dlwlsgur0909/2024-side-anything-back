package com.side.anything.back.companion.dto.response;

import com.side.anything.back.companion.entity.CompanionPost;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class CompanionPostListResponse {

    private List<CompanionPostResponse> postList;
    private Integer totalPages;

    public CompanionPostListResponse(List<CompanionPost> postList, Integer totalPages) {
        this.postList = postList.stream()
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
        private CompanionPostStatus status;

        public CompanionPostResponse(CompanionPost post) {
            this.id = post.getId();
            this.title = post.getTitle();
            this.location = post.getLocation();
            this.startDate = post.getStartDate();
            this.endDate = post.getEndDate();
            this.status = post.getStatus();
        }

    }
}
