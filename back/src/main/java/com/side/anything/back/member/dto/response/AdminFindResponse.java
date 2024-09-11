package com.side.anything.back.member.dto.response;

import com.side.anything.back.member.domain.Member;
import lombok.*;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminFindResponse {

    private String username;

    public AdminFindResponse(Member entity) {
        this.username = entity.getUsername();
    }
}
