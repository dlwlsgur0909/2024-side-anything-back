package com.side.anything.back.companion.dto.response;

import com.side.anything.back.companion.entity.CompanionApplication;
import com.side.anything.back.companion.entity.CompanionApplicationStatus;
import com.side.anything.back.companion.entity.CompanionPost;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class MyCompanionPostDetailResponse {

    private String title;
    private String content;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isClosed;

    private List<CompanionApplicationResponse> applicationList;

    public MyCompanionPostDetailResponse(CompanionPost post, List<CompanionApplication> applicationList) {
        this.title = post.getTitle();
        this.content = post.getContent();
        this.location = post.getLocation();
        this.startDate = post.getStartDate();
        this.endDate = post.getEndDate();
        this.isClosed = post.getStatus() == CompanionPostStatus.CLOSED;

        this.applicationList = applicationList.stream()
                .map(CompanionApplicationResponse::new)
                .toList();
    }

    @Getter
    private static class CompanionApplicationResponse {

        private Long id;
        private String message;
        private CompanionApplicationStatus status;
        private String nickname;
        private LocalDate dob;
        private String gender;
        private Boolean isPending;

        public CompanionApplicationResponse(CompanionApplication application) {
            this.id = application.getId();
            this.message = application.getMessage();
            this.status = application.getStatus();
            this.nickname = application.getMember().getNickname();
            this.dob = application.getMember().getDob();
            this.gender = application.getMember().getGender().equals("MALE") ? "남성" : "여성";
            this.isPending = application.getStatus() == CompanionApplicationStatus.PENDING;
        }

    }

}
