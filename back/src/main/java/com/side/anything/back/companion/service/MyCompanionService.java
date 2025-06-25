package com.side.anything.back.companion.service;

import com.side.anything.back.companion.dto.response.CompanionApplicationListResponse;
import com.side.anything.back.companion.dto.response.CompanionPostListResponse;
import com.side.anything.back.companion.dto.response.MyCompanionPostDetailResponse;
import com.side.anything.back.companion.entity.CompanionApplication;
import com.side.anything.back.companion.entity.CompanionApplicationStatus;
import com.side.anything.back.companion.entity.CompanionPost;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import com.side.anything.back.companion.repository.CompanionApplicationRepository;
import com.side.anything.back.companion.repository.CompanionPostRepository;
import com.side.anything.back.exception.BasicExceptionEnum;
import com.side.anything.back.exception.CustomException;
import com.side.anything.back.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyCompanionService {

    private static final int SIZE = 5;

    private final CompanionPostRepository postRepository;
    private final CompanionApplicationRepository applicationRepository;

    // 내 동행 모집 목록
    public CompanionPostListResponse findMyCompanionPostList(final TokenInfo tokenInfo,
                                                             final String keyword, final int page) {

        PageRequest pageRequest = PageRequest.of(page - 1, SIZE);

        Page<CompanionPost> pagedPost = postRepository.findMyPostList(
                keyword, CompanionPostStatus.DELETED, tokenInfo.getId(), pageRequest
        );

        return new CompanionPostListResponse(pagedPost.getContent(), pagedPost.getTotalPages());
    }

    // 내 동행 모집 상세
    public MyCompanionPostDetailResponse findMyCompanionPostDetail(final TokenInfo tokenInfo, final Long postId) {

        CompanionPost findPost = postRepository.findMyPostDetail(postId, tokenInfo.getId(), CompanionPostStatus.DELETED)
                .orElseThrow(() -> new CustomException(BasicExceptionEnum.NOT_FOUND, "모집 글을 찾을 수 없습니다"));

        List<CompanionApplication> findApplicationList = applicationRepository.findApplicationListByPost(
                findPost.getId(), List.of(CompanionApplicationStatus.PENDING, CompanionApplicationStatus.APPROVED)
        );

        return new MyCompanionPostDetailResponse(findPost, findApplicationList);
    }

    // 내 동행 신청 목록
    public CompanionApplicationListResponse findMyCompanionApplicationList(final TokenInfo tokenInfo, final int page) {

        PageRequest pageRequest = PageRequest.of(page - 1, SIZE);

        Page<CompanionApplication> pagedApplication = applicationRepository.findMyApplicationList(
                tokenInfo.getId(), pageRequest
        );

        return new CompanionApplicationListResponse(
                pagedApplication.getContent(), pagedApplication.getTotalPages()
        );

    }

    // 내 동행 신청 취소
    @Transactional
    public void cancelMyCompanionApplication(final TokenInfo tokenInfo, final Long applicationId) {

        CompanionApplication findApplication = findMyApplication(applicationId, tokenInfo.getId());

        if(findApplication.getCompanionPost().getStatus() != CompanionPostStatus.OPEN) {
            throw new CustomException(BasicExceptionEnum.FORBIDDEN, "이미 마감/삭제된 동행입니다");
        }

        if(!List.of(CompanionApplicationStatus.PENDING, CompanionApplicationStatus.APPROVED).contains(findApplication.getStatus())) {
            throw new CustomException(BasicExceptionEnum.FORBIDDEN, "대기/승인 상태의 신청만 취소 가능합니다");
        }

        findApplication.cancel();
    }

    // 내 동행 신청 삭제
    @Transactional
    public void deleteMyCompanionApplication(final TokenInfo tokenInfo, final Long applicationId) {

        CompanionApplication findApplication = findMyApplication(applicationId, tokenInfo.getId());

        if(findApplication.getIsDeleted()) {
            throw new CustomException(BasicExceptionEnum.FORBIDDEN, "이미 삭제된 동행 신청입니다");
        }

        findApplication.delete();
    }

    /* private methods */
    private CompanionApplication findMyApplication(Long applicationId, Long memberId) {
        return applicationRepository.findMyApplication(applicationId, memberId)
                .orElseThrow(() -> new CustomException(BasicExceptionEnum.NOT_FOUND, "신청 내역을 찾을 수 없습니다"));
    }

}
