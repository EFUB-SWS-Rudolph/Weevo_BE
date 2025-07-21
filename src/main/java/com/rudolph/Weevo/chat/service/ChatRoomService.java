package com.rudolph.Weevo.chat.service;

import com.rudolph.Weevo.auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.chat.domain.Chat;
import com.rudolph.Weevo.chat.domain.ChatRoom;
import com.rudolph.Weevo.chat.domain.enums.ChatCategory;
import com.rudolph.Weevo.chat.domain.enums.ChatType;
import com.rudolph.Weevo.chat.dto.request.ChatRoomCreateRequestDto;
import com.rudolph.Weevo.chat.dto.response.ChatRoomListResponseDto;
import com.rudolph.Weevo.chat.dto.response.ChatRoomStatusDto;
import com.rudolph.Weevo.chat.dto.summary.ChatRoomSummary;
import com.rudolph.Weevo.chat.repository.ChatRepository;
import com.rudolph.Weevo.chat.repository.ChatRoomRepository;
import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.repository.CourseRepository;
import com.rudolph.Weevo.global.common.code.ErrorStatus;
import com.rudolph.Weevo.global.exception.GeneralException;
import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public ChatRoomStatusDto checkChatRoomExist(CustomUserPrincipal user, Long opponentId, Long courseId) {
        Member sender = findMember(user.getMemberId());
        Member receiver = findReceiver(opponentId);

        Optional<ChatRoom> existingRoom;
        if (courseId != null) {
            Course course = findCourse(courseId);
            existingRoom = chatRoomRepository.findCourseChatRoom(sender.getId(), receiver.getId(), course.getId());
        } else {
            existingRoom = chatRoomRepository.findCoffeeChatRoom(sender.getId(), receiver.getId());
        }

        if (existingRoom.isPresent()) {
            return ChatRoomStatusDto.from(existingRoom.get().getId(), true);
        } else {
            return ChatRoomStatusDto.from(null, false);
        }
    }

    public void createChatRoom(CustomUserPrincipal user, ChatRoomCreateRequestDto request) {
        Member sender = findMember(user.getMemberId());
        Member receiver = findReceiver(request.getOpponentId());

        Course course;
        ChatCategory category;
        if (request.getCourseId() != null) {
            course = findCourse(request.getCourseId());
            category = ChatCategory.valueOf(course.getCourseType().name());
        } else {
            course = null;
            category = ChatCategory.COFFEECHAT;
        }

        ChatRoom newChatRoom = ChatRoom.builder()
                    .category(category)
                    .course(course)
                    .sender(sender)
                    .receiver(receiver)
                    .senderExited(false)
                    .receiverExited(false)
                    .build();
        chatRoomRepository.save(newChatRoom);


        Chat chat = Chat.builder()
                    .chatRoom(newChatRoom)
                    .sender(sender)
                    .type(ChatType.CHAT)
                    .content(request.getContent())
                    .build();
        chatRepository.save(chat);
    }

    @Transactional(readOnly = true)
    public ChatRoomListResponseDto getChatRooms(CustomUserPrincipal user, String category) {
        Member member = findMember(user.getMemberId());

        List<ChatRoom> chatRoomList;
        if (category == null || category.isBlank()) {
            chatRoomList = chatRoomRepository.findActiveChatRoomsByMember(member.getId());
        } else {
            ChatCategory chatCategory = ChatCategory.valueOf(category.toUpperCase());
            chatRoomList = chatRoomRepository.findActiveChatRoomsByCategoryAndMember(chatCategory, member.getId());
        }

        List<ChatRoomSummary> summaries = chatRoomList.stream()
                .map(chatRoom -> {
                    Member opponent = findOpponent(chatRoom, member.getId());
                    Chat lastChat = chatRepository.findTopByChatRoomOrderBySentAtDesc(chatRoom);
                    int unreadCount = chatRepository.countUnreadMessages(chatRoom, member.getId());

                    return ChatRoomSummary.from(chatRoom, opponent, lastChat, unreadCount);
                })
                .toList();

        return ChatRoomListResponseDto.from(summaries);
    }

    @Transactional(readOnly = true)
    private Member findOpponent(ChatRoom chatRoom, Long myId) {
        if (chatRoom.getSender().getId().equals(myId)) {
            return chatRoom.getReceiver();
        } else {
            return chatRoom.getSender();
        }
    }

    // 메세지 읽음 처리
    public void markMessagesAsRead(CustomUserPrincipal user, Long chatRoomId) {
        Member member = findMember(user.getMemberId());
        ChatRoom chatRoom = findChatRoom(chatRoomId);
        validateChatRoomAccess(chatRoom, member.getId());

        chatRepository.markAllUnreadMessagesAsRead(chatRoomId, member.getId());
    }

    // 채팅방 나가기
    public void leaveChatRoom(CustomUserPrincipal user, Long chatRoomId) {
        Member member = findMember(user.getMemberId());
        ChatRoom chatRoom = findChatRoom(chatRoomId);
        validateChatRoomAccess(chatRoom, member.getId());

        if (member.getId().equals(chatRoom.getSender().getId())) {
            chatRoom.setSenderExited();
        } else {
            chatRoom.setReceiverExited();
        }

        Chat systemMessage = Chat.builder()
                .chatRoom(chatRoom)
                .sender(null)
                .type(ChatType.SYSTEM)
                .content(member.getNickName() + "님이 채팅방을 떠났습니다.")
                .build();
        chatRepository.save(systemMessage);

        if (chatRoom.isSenderExited() && chatRoom.isReceiverExited()) {
            chatRoomRepository.delete(chatRoom);
        }
    }

    @Transactional(readOnly = true)
    public Member findMember(UUID memberId){
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(()-> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public ChatRoom findChatRoom(Long chatRoomId){
        return chatRoomRepository.findById(chatRoomId)
                    .orElseThrow(()-> new GeneralException(ErrorStatus.CHATROOM_NOT_FOUND));
    }

    // 채팅방 접근 권한 확인
    @Transactional(readOnly = true)
    public void validateChatRoomAccess(ChatRoom chatRoom, Long memberId) {
        boolean isSender = chatRoom.getSender().getId().equals(memberId);
        boolean isReceiver = chatRoom.getReceiver().getId().equals(memberId);

        if (!isSender && !isReceiver) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_CHATROOM_ACCESS);
        }
        if ((isSender && chatRoom.isSenderExited()) ||
                (isReceiver && chatRoom.isReceiverExited())) {
            throw new GeneralException(ErrorStatus.CHATROOM_ALREADY_EXITED);
        }
    }

    private Member findReceiver(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    private Course findCourse(Long userId) {
        return courseRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COURSE_NOT_FOUND));
    }
}