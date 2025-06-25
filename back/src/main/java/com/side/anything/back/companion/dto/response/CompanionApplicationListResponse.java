package com.side.anything.back.companion.dto.response;

import com.side.anything.back.companion.entity.CompanionApplication;
import com.side.anything.back.companion.entity.CompanionApplicationStatus;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class CompanionApplicationListResponse {

    private List<CompanionApplicationResponse> applicationList;
    private Integer totalPages;

    public CompanionApplicationListResponse(List<CompanionApplication> applicationList, Integer totalPages) {
        this.applicationList = applicationList.stream()
                .map(CompanionApplicationResponse::new)
                .toList();
        this.totalPages = totalPages;
    }

    @Getter
    private static class CompanionApplicationResponse {

        private Long applicationId;
        private CompanionApplicationStatus applicationStatus;
        private Boolean isCancelable;
        private Long postId;
        private String postTitle;
        private String postLocation;
        private String postStatus;

        public CompanionApplicationResponse(CompanionApplication application) {
            this.applicationId = application.getId();
            this.applicationStatus = application.getStatus();
            this.isCancelable = checkIsCancelable(application.getStatus(), application.getCompanionPost().getStatus());
            this.postId = application.getCompanionPost().getId();
            this.postTitle = application.getCompanionPost().getTitle();
            this.postLocation = application.getCompanionPost().getLocation();
            this.postStatus = application.getCompanionPost().getStatus().getDescription();
        }

        private Boolean checkIsCancelable(CompanionApplicationStatus applicationStatus, CompanionPostStatus postStatus) {
            return List.of(CompanionApplicationStatus.PENDING, CompanionApplicationStatus.APPROVED).contains(applicationStatus)
                    && postStatus == CompanionPostStatus.OPEN;
        }
    }

}
