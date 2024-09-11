package com.side.anything.back.member.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminMemberListResponse {

    List<MemberDetailResponse> memberList = new ArrayList<>();

}
