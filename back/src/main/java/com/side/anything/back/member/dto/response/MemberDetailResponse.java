package com.side.anything.back.member.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.side.anything.back.member.entity.Member;
import lombok.*;

import java.time.LocalDateTime;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDetailResponse {

    private String name;
    private String email;
    private Boolean isSnsMember;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    public MemberDetailResponse(Member entity) {
        this.name = entity.getName();
        this.email = entity.getEmail();
        this.createdAt = entity.getCreatedAt();
    }

    public void setIsSnsMember(boolean isSnsMember) {
        this.isSnsMember = isSnsMember;
    }
}
