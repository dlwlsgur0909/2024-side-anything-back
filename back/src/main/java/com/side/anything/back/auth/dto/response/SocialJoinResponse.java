package com.side.anything.back.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SocialJoinResponse {

    private String nickname;
    private Boolean isProfileCompleted;

}
