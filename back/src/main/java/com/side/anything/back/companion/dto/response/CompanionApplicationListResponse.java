package com.side.anything.back.companion.dto.response;

import com.side.anything.back.companion.entity.CompanionApplication;
import com.side.anything.back.companion.entity.CompanionApplicationStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class CompanionApplicationListResponse {

    private List<CompanionApplicationResponse> myCompanionApplicationList;
    private Integer totalPages;

    public CompanionApplicationListResponse(List<CompanionApplication> companionApplicationList, Integer totalPages) {
        this.myCompanionApplicationList = companionApplicationList.stream()
                .map(CompanionApplicationResponse::new)
                .toList();
        this.totalPages = totalPages;
    }

    @Getter
    static class CompanionApplicationResponse {

        private Long applicationId;
        private String applicationStatus;
        private Boolean isCancelable;
        private Long postId;
        private String postTitle;
        private String postLocation;
        private String postStatus;

        public CompanionApplicationResponse(CompanionApplication companionApplication) {
            this.applicationId = companionApplication.getId();
            this.applicationStatus = companionApplication.getStatus().getDescription();
            this.isCancelable = List.of(
                        CompanionApplicationStatus.PENDING, CompanionApplicationStatus.APPROVED
                    )
                    .contains(companionApplication.getStatus());
            this.postId = companionApplication.getCompanionPost().getId();
            this.postTitle = companionApplication.getCompanionPost().getTitle();
            this.postLocation = companionApplication.getCompanionPost().getLocation();
            this.postStatus = companionApplication.getCompanionPost().getStatus().getDescription();
        }
    }

}
