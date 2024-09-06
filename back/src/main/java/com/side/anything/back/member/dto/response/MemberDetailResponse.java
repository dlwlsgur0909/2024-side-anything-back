package com.side.anything.back.member.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.side.anything.back.member.domain.Member;
import lombok.*;

import java.time.LocalDateTime;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDetailResponse {

    private String username;
    private String email;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    public MemberDetailResponse(Member entity) {
        this.username = entity.getUsername();
        this.email = entity.getEmail();
        this.createdAt = entity.getCreatedAt();
    }
}
