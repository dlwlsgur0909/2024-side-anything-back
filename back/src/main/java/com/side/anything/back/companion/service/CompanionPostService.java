package com.side.anything.back.companion.service;

import com.side.anything.back.companion.dto.request.CompanionPostSaveRequest;
import com.side.anything.back.companion.entity.CompanionPost;
import com.side.anything.back.companion.repository.CompanionPostRepository;
import com.side.anything.back.exception.CustomException;
import com.side.anything.back.member.entity.Member;
import com.side.anything.back.member.repository.MemberRepository;
import com.side.anything.back.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.side.anything.back.exception.BasicExceptionEnum.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanionPostService {

    private final CompanionPostRepository companionPostRepository;
    private final MemberRepository memberRepository;

    // 동행 모집 글 목록
    public List<CompanionPost> findCompanionPostList() {
        return companionPostRepository.findAll();
    }

    // 동행 모집 글 단건 조회
    public CompanionPost findCompanionPost(final Long id) {

        return findCompanionPostById(id);
    }

    // 동행 모집 글 저장
    @Transactional
    public void saveCompanionPost(final TokenInfo tokenInfo, final CompanionPostSaveRequest request) {

        Member findMember = findMemberById(tokenInfo.getId());

        // 시작일 오늘 날짜 검증
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new CustomException(BAD_REQUEST, "시작일은 오늘 이전일 수 없습니다");
        }

        // 시작일, 종료일 검증
        if(request.getStartDate().isAfter(request.getEndDate())) {
            throw new CustomException(BAD_REQUEST, "시작일은 종료일 이후일 수 없습니다");
        }

        CompanionPost companionPost = CompanionPost.of(request, findMember);
        companionPostRepository.save(companionPost);
    }

    // 동행 모집 글 수정
    @Transactional
    public void updateCompanionPost(final TokenInfo tokenInfo, final Long companionPostId,
                                    final CompanionPostSaveRequest request) {

        CompanionPost findCompanionPost = findCompanionPostById(companionPostId);

        // 작성자 검증
        if(!findCompanionPost.getWriter().getId().equals(tokenInfo.getId())) {
            throw new CustomException(FORBIDDEN, "작성자만 수정할 수 있습니다");
        }

        // 마감 여부 검증
        if(findCompanionPost.getIsClosed()) {
            throw new CustomException(FORBIDDEN, "마감된 모집은 수정할 수 없습니다");
        }

        // 시작일 오늘 날짜 검증
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new CustomException(BAD_REQUEST, "시작일은 오늘 이전일 수 없습니다");
        }

        // 시작일, 종료일 검증
        if(request.getStartDate().isAfter(request.getEndDate())) {
            throw new CustomException(BAD_REQUEST, "시작일은 종료일 이전일 수 없습니다");
        }

        findCompanionPost.update(request);

        // 삭제 시 신청 내역 초기화

    }

    @Transactional
    public void deleteCompanionPost(final Long companionPostId) {
        companionPostRepository.deleteById(companionPostId);
    }


    /* private methods */

    private CompanionPost findCompanionPostById(final Long id) {
        return companionPostRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "모집 글을 찾을 수 없습니다"));
    }

    private Member findMemberById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "회원 정보를 찾을 수 없습니다"));
    }

}
