package com.rudolph.Weevo.chat.service;

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
import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public ChatRoomStatusDto checkChatRoomExist(Long opponentId, Long courseId) {
        Member sender = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
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

    public void createChatRoom(ChatRoomCreateRequestDto request) {
        Member sender = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
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
    public ChatRoomListResponseDto getChatRooms(String category) {
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

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
    public void markMessagesAsRead(Long chatRoomId) {
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        ChatRoom chatRoom = findChatRoom(chatRoomId);
        validateChatRoomAccess(chatRoom, member.getId());

        chatRepository.markAllUnreadMessagesAsRead(chatRoomId, member.getId());
    }

    // 채팅방 나가기
    public void leaveChatRoom(Long chatRoomId) {
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

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
                .content(member.getName() + "님이 채팅방을 떠났습니다.")
                .build();
        chatRepository.save(systemMessage);

        if (chatRoom.isSenderExited() && chatRoom.isReceiverExited()) {
            chatRoomRepository.delete(chatRoom);
        }
    }

    @Transactional(readOnly = true)
    public ChatRoom findChatRoom(Long chatRoomId){
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()-> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
    }

    // 채팅방 접근 권한 확인
    @Transactional(readOnly = true)
    public void validateChatRoomAccess(ChatRoom chatRoom, Long memberId) {
        boolean isSender = chatRoom.getSender().getId().equals(memberId);
        boolean isReceiver = chatRoom.getReceiver().getId().equals(memberId);

        if (!isSender && !isReceiver) {
            throw new IllegalArgumentException("채팅방에 접근할 권한이 없습니다.");
        }
        if ((isSender && chatRoom.isSenderExited()) ||
                (isReceiver && chatRoom.isReceiverExited())) {
            throw new IllegalStateException("이미 나간 채팅방에는 접근할 수 없습니다.");
        }
    }

    private Member findReceiver(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID = " + id));
    }

    private Course findCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다. ID = " + id));
    }
}