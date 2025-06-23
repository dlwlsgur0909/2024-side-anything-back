package com.side.anything.back.companion.service;

import com.side.anything.back.companion.dto.response.CompanionApplicationListResponse;
import com.side.anything.back.companion.dto.response.CompanionPostListResponse;
import com.side.anything.back.companion.entity.CompanionApplication;
import com.side.anything.back.companion.entity.CompanionApplicationStatus;
import com.side.anything.back.companion.entity.CompanionPost;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import com.side.anything.back.companion.repository.CompanionApplicationRepository;
import com.side.anything.back.companion.repository.CompanionPostRepository;
import com.side.anything.back.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyCompanionService {

    private static final int SIZE = 5;

    private final CompanionPostRepository companionPostRepository;
    private final CompanionApplicationRepository companionApplicationRepository;

    // 내 동행 모집 목록
    public CompanionPostListResponse findMyCompanionPostList(final TokenInfo tokenInfo,
                                                             final String keyword, final int page) {

        PageRequest pageRequest = PageRequest.of(page - 1, SIZE);

        Page<CompanionPost> pagedCompanionPost = companionPostRepository.findMyPostList(
                keyword, CompanionPostStatus.DELETED, tokenInfo.getId(), pageRequest
        );

        return new CompanionPostListResponse(pagedCompanionPost.getContent(), pagedCompanionPost.getTotalPages());
    }

    // 내 동행 신청 목록
    public CompanionApplicationListResponse findMyCompanionApplicationList(final TokenInfo tokenInfo, final int page) {

        PageRequest pageRequest = PageRequest.of(page - 1, SIZE);

        Page<CompanionApplication> pagedCompanionApplication = companionApplicationRepository.findMyApplicationList(
                tokenInfo.getId(), CompanionApplicationStatus.DELETED, pageRequest
        );

        return new CompanionApplicationListResponse(
                pagedCompanionApplication.getContent(), pagedCompanionApplication.getTotalPages()
        );

    }


}
