package com.side.anything.back.companion.service;

import com.side.anything.back.chat.dto.response.ChatMessageResponse;
import com.side.anything.back.chat.entity.ChatMessage;
import com.side.anything.back.chat.entity.ChatParticipant;
import com.side.anything.back.chat.entity.ChatRoom;
import com.side.anything.back.chat.entity.MessageType;
import com.side.anything.back.chat.repository.ChatMessageRepository;
import com.side.anything.back.chat.repository.ChatParticipantRepository;
import com.side.anything.back.chat.repository.ChatRoomRepository;
import com.side.anything.back.companion.dto.request.CompanionApplicationSaveRequest;
import com.side.anything.back.companion.dto.request.CompanionApplicationUpdateStatusRequest;
import com.side.anything.back.companion.dto.request.CompanionPostSaveRequest;
import com.side.anything.back.companion.dto.response.CompanionPostDetailResponse;
import com.side.anything.back.companion.dto.response.CompanionPostListResponse;
import com.side.anything.back.companion.entity.CompanionApplication;
import com.side.anything.back.companion.entity.CompanionApplicationStatus;
import com.side.anything.back.companion.entity.CompanionPost;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import com.side.anything.back.companion.repository.CompanionApplicationRepository;
import com.side.anything.back.companion.repository.CompanionPostRepository;
import com.side.anything.back.config.RedisPublisher;
import com.side.anything.back.exception.BasicExceptionEnum;
import com.side.anything.back.exception.CustomException;
import com.side.anything.back.member.entity.Member;
import com.side.anything.back.member.repository.MemberRepository;
import com.side.anything.back.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
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

    private final CompanionPostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CompanionApplicationRepository applicationRepository;

    private final ChatRoomRepository roomRepository;
    private final ChatParticipantRepository participantRepository;
    private final ChatMessageRepository messageRepository;

    private final RedisPublisher redisPublisher;
    private final SimpMessageSendingOperations messagingTemplate;

    // 동행 모집 목록
    public CompanionPostListResponse findCompanionPostList(final TokenInfo tokenInfo, final String keyword, final int page) {

        PageRequest pageRequest = PageRequest.of(page - 1, SIZE);

        Page<CompanionPost> pagedPost = postRepository.findPostList(
                keyword, CompanionPostStatus.DELETED, tokenInfo.getId(), pageRequest
        );

        return new CompanionPostListResponse(pagedPost.getContent(), pagedPost.getTotalPages());
    }

    // 동행 모집 상세 조회
    public CompanionPostDetailResponse findCompanionPostDetail(final TokenInfo tokenInfo, final Long postId) {

        CompanionPost findPost = findPostDetailById(postId);

        if(findPost.getMember().getId().equals(tokenInfo.getId())) {
            throw new CustomException(FORBIDDEN, "내가 작성한 동행은 '내 동행 모집'에서 확인해주세요");
        }

        return new CompanionPostDetailResponse(findPost, checkIsApplied(tokenInfo.getId(), postId));
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

        // 동행 모집 저장
        CompanionPost savedCompanionPost = postRepository.save(CompanionPost.of(request, findMember));

        // 동행 모집에 연결된 채팅방 생성
        ChatRoom savedChatRoom = roomRepository.save(ChatRoom.of(savedCompanionPost));

        // 채팅방에 동행 모집 작성자 추가
        participantRepository.save(ChatParticipant.of(savedChatRoom, findMember, true));

        // 채팅방 입장 메세지 저장
        messageRepository.save(
                ChatMessage.of(savedChatRoom, findMember, findMember.getNickname() + "님이 입장했습니다", MessageType.ENTER)
        );

    }

    // 동행 모집 마감
    @Transactional
    public void closeCompanionPost(final TokenInfo tokenInfo, final Long postId) {

        CompanionPost findPost = findPostDetailById(postId);

        // 작성자 검증
        if(!findPost.getMember().getId().equals(tokenInfo.getId())) {
            throw new CustomException(FORBIDDEN, "모집 마감은 작성자만 가능합니다");
        }

        // 마감 여부 검증
        if(findPost.getStatus() != CompanionPostStatus.OPEN) {
            throw new CustomException(FORBIDDEN, "이미 마감된 동행입니다");
        }

        findPost.close();
    }

    // 동행 모집 삭제
    @Transactional
    public void deleteCompanionPost(final TokenInfo tokenInfo, final Long postId) {

        CompanionPost findPost = findPostDetailById(postId);

        // 작성자 검증
        if(!findPost.getMember().getId().equals(tokenInfo.getId())) {
            throw new CustomException(FORBIDDEN, "동행 삭제는 작성자만 가능합니다");
        }

        // 마감전이면 신청 내역에 반영
        if(findPost.getStatus() == CompanionPostStatus.OPEN) {
            applicationRepository.cancelByHost(
                    postId,
                    CompanionApplicationStatus.CANCELLED_BY_HOST,
                    List.of(CompanionApplicationStatus.PENDING, CompanionApplicationStatus.APPROVED)
            );
        }

        // 동행 모집 삭제
        findPost.delete();

        // 채팅방 삭제
        ChatRoom findChatRoom = roomRepository.findChatRoomByPost(findPost.getId())
                .orElseThrow(() -> new CustomException(NOT_FOUND, "해당 동행에 연결된 채팅방이 없습니다"));

        findChatRoom.delete();

    }

    // 동행 신청
    @Transactional
    public void saveCompanionApplication(final TokenInfo tokenInfo, final Long postId,
                                         final CompanionApplicationSaveRequest request) {

        Member findMember = findMemberById(tokenInfo.getId());
        CompanionPost findPost = findPostById(postId);

        if (checkIsApplied(findMember.getId(), findPost.getId())) {
            throw new CustomException(BAD_REQUEST, "이미 지원한 동행입니다");
        }

        if(findPost.getStatus() != CompanionPostStatus.OPEN) {
            throw new CustomException(FORBIDDEN, "마감된 동행입니다");
        }

        if(findMember.getId().equals(findPost.getMember().getId())) {
            throw new CustomException(FORBIDDEN, "작성자는 신청할 수 없습니다");
        }

        applicationRepository.save(CompanionApplication.of(request, findMember, findPost));
    }

    // 동행 신청 승인/거절
    @Transactional
    public void updateCompanionApplicationStatus(final TokenInfo tokenInfo,
                                                 final Long postId,
                                                 final Long applicationId,
                                                 final CompanionApplicationUpdateStatusRequest request) {

        CompanionApplication findApplication = applicationRepository.findApplicationByPost(
                postId, CompanionPostStatus.OPEN, tokenInfo.getId(),
                applicationId, CompanionApplicationStatus.PENDING
        ).orElseThrow(() -> new CustomException(BasicExceptionEnum.NOT_FOUND));

        if(request.getIsApproval()) {
            // 승인
            findApplication.approve();

            // 채팅방에 승인된 참가자 추가
            Member findMember = findMemberById(findApplication.getMember().getId());

            // 채팅방 조회
            ChatRoom findChatRoom = roomRepository.findChatRoomByPost(postId)
                    .orElseThrow(() -> new CustomException(NOT_FOUND, "해당 동행에 연결된 채팅방이 없습니다"));

            participantRepository.save(ChatParticipant.of(findChatRoom, findMember, false));

            // 참가자 채팅방 입장 메세지 저장
            ChatMessage saveChatMessage = messageRepository.save(
                    ChatMessage.of(findChatRoom, findMember, findMember.getNickname() + "님이 입장했습니다", MessageType.ENTER)
            );

            // ChatMessageResponse 객체 생성
            ChatMessageResponse response = new ChatMessageResponse(saveChatMessage);

            // Redis publish
            redisPublisher.publish(response);

        }else {
            // 거절
            findApplication.reject();
        }

    }

    /* private methods */

    // 동행 모집 조회 상세
    private CompanionPost findPostDetailById(final Long postId) {
        return postRepository.findPostDetail(postId, CompanionPostStatus.DELETED)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "모집 글을 찾을 수 없습니다"));
    }

    // 동행 모집 조회 단건
    private CompanionPost findPostById(final Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "모집 글을 찾을 수 없습니다"));
    }

    // 회원 조회 단건
    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "회원 정보를 찾을 수 없습니다"));
    }

    // 신청 여부 확인
    private Boolean checkIsApplied(Long memberId, Long postId) {
        return applicationRepository.isApplied(
                memberId, postId,
                List.of(
                        CompanionApplicationStatus.PENDING,
                        CompanionApplicationStatus.APPROVED,
                        CompanionApplicationStatus.REJECTED
                )
        );
    }

}
