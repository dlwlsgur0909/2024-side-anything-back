package com.side.anything.back.companion.dto.response;

import com.side.anything.back.companion.entity.CompanionApplication;
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

    public MyCompanionPostDetailResponse(CompanionPost companionPost, List<CompanionApplication> applicationList) {
        this.title = companionPost.getTitle();
        this.content = companionPost.getContent();
        this.location = companionPost.getLocation();
        this.startDate = companionPost.getStartDate();
        this.endDate = companionPost.getEndDate();
        this.isClosed = companionPost.getStatus() == CompanionPostStatus.CLOSED;

        this.applicationList = applicationList.stream()
                .map(CompanionApplicationResponse::new)
                .toList();
    }

    @Getter
    private static class CompanionApplicationResponse {

        private Long id;
        private String message;
        private String status;
        private String nickname;
        private LocalDate dob;
        private String gender;

        public CompanionApplicationResponse(CompanionApplication application) {
            this.id = application.getId();
            this.message = application.getMessage();
            this.status = application.getStatus().getDescription();
            this.nickname = application.getMember().getNickname();
            this.dob = application.getMember().getDob();
            this.gender = application.getMember().getGender().equals("MALE") ? "남성" : "여성";
        }

    }

}
