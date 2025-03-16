package com.side.anything.back.member.service;

import com.side.anything.back.exception.BasicCustomException;
import com.side.anything.back.jwt.TokenInfo;
import com.side.anything.back.member.domain.Member;
import com.side.anything.back.member.domain.Role;
import com.side.anything.back.member.dto.response.AdminFindResponse;
import com.side.anything.back.member.dto.response.AdminMemberListResponse;
import com.side.anything.back.member.dto.response.MemberDetailResponse;
import com.side.anything.back.member.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminFindResponse findAdmin(final TokenInfo tokenInfo) {

        Member findAdmin = adminRepository.findById(tokenInfo.getId())
                .orElseThrow(() -> new BasicCustomException(HttpStatus.NOT_FOUND, "404", "관리자 정보를 찾을 수 없습니다"));


        return new AdminFindResponse(findAdmin);
    }

    public AdminMemberListResponse findMemberList() {

        List<Member> findMemberList = adminRepository.findMemberList(Role.ROLE_USER);

        List<MemberDetailResponse> memberDetailList = findMemberList.stream()
                .map(MemberDetailResponse::new)
                .toList();

        return AdminMemberListResponse.builder()
                .memberList(memberDetailList)
                .build();
    }

}
