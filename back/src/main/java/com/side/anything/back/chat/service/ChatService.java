package com.side.anything.back.chat.service;

import com.side.anything.back.chat.dto.response.ChatExceptionResponse;
import com.side.anything.back.chat.dto.response.ChatMessageResponse;
import com.side.anything.back.chat.dto.response.ChatRoomEnterResponse;
import com.side.anything.back.chat.dto.response.ChatRoomListResponse;
import com.side.anything.back.chat.entity.ChatMessage;
import com.side.anything.back.chat.entity.ChatParticipant;
import com.side.anything.back.chat.entity.ChatRoom;
import com.side.anything.back.chat.entity.MessageType;
import com.side.anything.back.chat.repository.ChatMessageRepository;
import com.side.anything.back.chat.repository.ChatParticipantRepository;
import com.side.anything.back.chat.repository.ChatRoomRepository;
import com.side.anything.back.companion.entity.CompanionApplication;
import com.side.anything.back.companion.entity.CompanionApplicationStatus;
import com.side.anything.back.companion.entity.CompanionPostStatus;
import com.side.anything.back.companion.repository.CompanionApplicationRepository;
import com.side.anything.back.config.RedisPublisher;
import com.side.anything.back.exception.CustomException;
import com.side.anything.back.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.side.anything.back.exception.BasicExceptionEnum.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private static final int SIZE = 5;

    private final ChatRoomRepository roomRepository;
    private final ChatParticipantRepository participantRepository;
    private final ChatMessageRepository messageRepository;

    private final CompanionApplicationRepository applicationRepository;

    private final RedisPublisher redisPublisher;
    private final SimpMessageSendingOperations messagingTemplate;

    // 채팅방 목록 조회
    public ChatRoomListResponse findChatRoomList(final TokenInfo tokenInfo,
                                                 final String keyword,
                                                 final int page) {

        PageRequest pageRequest = PageRequest.of(page - 1, SIZE);

        Page<ChatParticipant> pagedChatParticipantList = participantRepository
                .searchMyChatRoomList(keyword, tokenInfo.getId(), CompanionPostStatus.DELETED, pageRequest);

        return new ChatRoomListResponse(pagedChatParticipantList.getContent(), pagedChatParticipantList.getTotalPages());
    }

    // 채팅 내역과 참가자 조회
    public ChatRoomEnterResponse enterChatRoom(final TokenInfo tokenInfo, final Long roomId) {

        Boolean isParticipant = participantRepository.isParticipant(roomId, tokenInfo.getId());

        if(!isParticipant) {
            throw new CustomException(NOT_FOUND, "채팅방을 찾을 수 없습니다");
        }

        // 채팅방 조회
        ChatRoom findChatRoom = roomRepository.findWithPost(roomId, CompanionPostStatus.DELETED)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "존재하지 않는 동행입니다"));

        /*
        채팅방 조회 시 동행 상태와 채팅방의 active를 확인하기 때문에 여기서는 확인하지 않아도 되겠지만
        참가자 조회를 다른 곳에서 사용할 수 도 있어서 한번 더 검증
         */
        // 채팅방 참가자 조회
        List<ChatParticipant> participantList = participantRepository.findParticipantList(roomId, CompanionPostStatus.DELETED);

        // 메세지 내역 조회
        List<ChatMessage> messageList = messageRepository.findMessageList(roomId, tokenInfo.getId(), MessageType.ENTER);

        return new ChatRoomEnterResponse(findChatRoom.getCompanionPost().getTitle(), participantList, messageList);
    }

    // 참가자 강퇴
    @Transactional
    public void banChatParticipant(final TokenInfo tokenInfo, final Long roomId, final Long participantId) {

        try {
            // 채팅방 조회
            ChatRoom findChatRoom = roomRepository.findWithPost(roomId, CompanionPostStatus.DELETED)
                    .orElseThrow(() -> new CustomException(NOT_FOUND, "채팅방을 찾을 수 없습니다"));

            // 방장 조회
            ChatParticipant findHost = participantRepository.findHost(roomId, tokenInfo.getId(), CompanionPostStatus.DELETED)
                    .orElseThrow(() -> new CustomException(NOT_FOUND, "방장을 찾을 수 없습니다"));

            // 채팅방 참가자 조회
            ChatParticipant findParticipant = participantRepository.findParticipant(roomId, participantId, CompanionPostStatus.DELETED)
                    .orElseThrow(() -> new CustomException(NOT_FOUND, "참가자를 찾을 수 없습니다"));

            findParticipant.leave();

            // 참가자 동행 모집 신청 상태 변경
            CompanionApplication findApplication = applicationRepository.findApplicationForLeave(
                    findChatRoom.getCompanionPost().getId(), findParticipant.getMember().getId(),
                    CompanionPostStatus.DELETED, CompanionApplicationStatus.APPROVED
            ).orElseThrow(() -> new CustomException(NOT_FOUND, "동행 신청 내역을 찾을 수 없습니다"));

            findApplication.cancelledByHost();

            // 참가자 퇴장 메세지 저장
            ChatMessage saveChatMessage = messageRepository.save(
                    ChatMessage.of(findChatRoom, findHost.getMember(), findParticipant.getMember().getNickname() + "님이 퇴장되었습니다", MessageType.BANNED)
            );

            // ChatMessageResponse 객체 생성
            ChatMessageResponse response = new ChatMessageResponse(saveChatMessage);

            // Redis publish
            redisPublisher.publish(response);
        }catch (CustomException ce) {
            ChatExceptionResponse chatExceptionResponse = new ChatExceptionResponse(tokenInfo.getId(), ce.getErrorCode(), ce.getErrorMessage());
            String destination = "/sub/chat/" + roomId + "/errors";
            messagingTemplate.convertAndSend(destination, chatExceptionResponse);
        }

    }

    // 채팅방 나가기
    @Transactional
    public void leaveChatRoom(final TokenInfo tokenInfo, final Long roomId) {
        try {
            // 채팅방 조회
            ChatRoom findChatRoom = roomRepository.findWithPost(roomId, CompanionPostStatus.DELETED)
                    .orElseThrow(() -> new CustomException(NOT_FOUND, "채팅방을 찾을 수 없습니다"));

            // 채팅방 참가자 조회
            ChatParticipant findParticipant = participantRepository.findSelf(roomId, tokenInfo.getId(), CompanionPostStatus.DELETED)
                    .orElseThrow(() -> new CustomException(NOT_FOUND, "해당 채팅방의 참가자가 아닙니다"));

            findParticipant.leave();

            // 방장인 경우와 아닌 경우 분기 처리
            if(findParticipant.getIsHost()) {
                // 마감전이면 신청 내역에 반영
                if(findChatRoom.getCompanionPost().getStatus() == CompanionPostStatus.OPEN) {
                    applicationRepository.cancelByHost(
                            findChatRoom.getCompanionPost().getId(),
                            CompanionApplicationStatus.CANCELLED_BY_HOST,
                            List.of(CompanionApplicationStatus.PENDING, CompanionApplicationStatus.APPROVED)
                    );
                }

                // 동행 모집 삭제
                findChatRoom.getCompanionPost().delete();

                // 채팅방 삭제
                findChatRoom.delete();

            }else {
                // 참가자 동행 모집 신청 상태 변경
                CompanionApplication findApplication = applicationRepository.findApplicationForLeave(
                        findChatRoom.getCompanionPost().getId(), findParticipant.getMember().getId(),
                        CompanionPostStatus.DELETED, CompanionApplicationStatus.APPROVED
                ).orElseThrow(() -> new CustomException(NOT_FOUND, "동행 신청 내역을 찾을 수 없습니다"));

                findApplication.cancel();
            }

            // 참가자 퇴장 메세지 저장
            ChatMessage saveChatMessage = messageRepository.save(
                    ChatMessage.of(findChatRoom, findParticipant.getMember(), findParticipant.getMember().getNickname() + "님이 나갔습니다", MessageType.LEAVE)
            );

            // ChatMessageResponse 객체 생성
            ChatMessageResponse response = new ChatMessageResponse(saveChatMessage);

            // Redis publish
            redisPublisher.publish(response);

        }catch (CustomException ce) {
            ChatExceptionResponse chatExceptionResponse = new ChatExceptionResponse(tokenInfo.getId(), ce.getErrorCode(), ce.getErrorMessage());
            String destination = "/sub/chat/" + roomId + "/errors";
            messagingTemplate.convertAndSend(destination, chatExceptionResponse);
        }
    }


}
