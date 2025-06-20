package com.side.anything.back.companion.service;

import com.side.anything.back.companion.dto.request.CompanionApplicationSaveRequest;
import com.side.anything.back.companion.dto.request.CompanionPostSaveRequest;
import com.side.anything.back.companion.dto.response.CompanionPostDetailResponse;
import com.side.anything.back.companion.dto.response.CompanionPostListResponse;
import com.side.anything.back.companion.entity.CompanionApplication;
import com.side.anything.back.companion.entity.CompanionApplicationStatus;
import com.side.anything.back.companion.entity.CompanionPost;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import com.side.anything.back.companion.repository.CompanionApplicationRepository;
import com.side.anything.back.companion.repository.CompanionPostRepository;
import com.side.anything.back.exception.CustomException;
import com.side.anything.back.member.entity.Member;
import com.side.anything.back.member.repository.MemberRepository;
import com.side.anything.back.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.side.anything.back.exception.BasicExceptionEnum.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanionService {

    private static final int SIZE = 5;

    private final CompanionPostRepository companionPostRepository;
    private final MemberRepository memberRepository;
    private final CompanionApplicationRepository companionApplicationRepository;

    // 동행 모집 목록
    public CompanionPostListResponse findCompanionPostList(final String keyword, final int page) {

        PageRequest pageRequest = PageRequest.of(page - 1, SIZE);

        Page<CompanionPost> pagedCompanionPost = companionPostRepository.findPagedList(
                keyword, CompanionPostStatus.DELETED, pageRequest
        );

        return new CompanionPostListResponse(pagedCompanionPost.getContent(), pagedCompanionPost.getTotalPages());
    }

    // 동행 모집 상세 조회
    public CompanionPostDetailResponse findCompanionPostDetail(final TokenInfo tokenInfo, final Long companionPostId) {

        return new CompanionPostDetailResponse(
                findCompanionPostDetailById(companionPostId),
                checkIsApplied(tokenInfo.getId(), companionPostId)
        );
    }

    // 동행 모집 저장
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

    // 동행 모집 마감
    @Transactional
    public void closeCompanionPost(final TokenInfo tokenInfo, final Long companionPostId) {

        CompanionPost findCompanionPost = findCompanionPostDetailById(companionPostId);

        // 작성자 검증
        if(!findCompanionPost.getMember().getId().equals(tokenInfo.getId())) {
            throw new CustomException(FORBIDDEN, "모집 마감은 작성자만 가능합니다");
        }

        // 마감 여부 검증
        if(findCompanionPost.getStatus() != CompanionPostStatus.OPEN) {
            throw new CustomException(FORBIDDEN, "이미 마감된 동행입니다");
        }

        findCompanionPost.close();
    }

    // 동행 모집 삭제
    @Transactional
    public void deleteCompanionPost(final TokenInfo tokenInfo, final Long companionPostId) {

        CompanionPost findCompanionPost = findCompanionPostDetailById(companionPostId);

        // 작성자 검증
        if(!findCompanionPost.getMember().getId().equals(tokenInfo.getId())) {
            throw new CustomException(FORBIDDEN, "동행 삭제는 작성자만 가능합니다");
        }

        // 마감전이면 신청 내역에 반영
        if(findCompanionPost.getStatus() == CompanionPostStatus.OPEN) {
            companionApplicationRepository.cancelByHost(
                    companionPostId,
                    CompanionApplicationStatus.CANCELLED_BY_HOST,
                    List.of(CompanionApplicationStatus.PENDING, CompanionApplicationStatus.APPROVED)
            );
            }

        findCompanionPost.delete();
    }

    // 동행 신청
    @Transactional
    public void saveCompanionApplication(final TokenInfo tokenInfo, final Long companionPostId,
                                         final CompanionApplicationSaveRequest request) {

        Member findMember = findMemberById(tokenInfo.getId());
        CompanionPost findCompanionPost = findCompanionPostById(companionPostId);

        if (checkIsApplied(findMember.getId(), findCompanionPost.getId())) {
            throw new CustomException(BAD_REQUEST, "이미 지원한 동행입니다");
        }

        if(findCompanionPost.getStatus() != CompanionPostStatus.OPEN) {
            throw new CustomException(FORBIDDEN, "마감된 동행입니다");
        }

        if(findMember.getId().equals(findCompanionPost.getMember().getId())) {
            throw new CustomException(FORBIDDEN, "작성자는 신청할 수 없습니다");
        }

        companionApplicationRepository.save(CompanionApplication.of(request, findMember, findCompanionPost));
    }


    /* private methods */

    // 동행 모집 조회 상세
    private CompanionPost findCompanionPostDetailById(final Long id) {
        return companionPostRepository.findDetailById(id, CompanionPostStatus.DELETED)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "모집 글을 찾을 수 없습니다"));
    }

    // 동행 모집 조회 단건
    private CompanionPost findCompanionPostById(final Long id) {
        return companionPostRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "모집 글을 찾을 수 없습니다"));
    }

    // 회원 조회 단건
    private Member findMemberById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "회원 정보를 찾을 수 없습니다"));
    }

    // 신청 여부 확인
    private Boolean checkIsApplied(Long memberId, Long companionPostId) {
        return companionApplicationRepository.existsByMemberIdAndCompanionPostId(memberId, companionPostId);
    }

}
