package com.side.anything.back.member.dto.response;

import com.side.anything.back.member.entity.Member;
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
