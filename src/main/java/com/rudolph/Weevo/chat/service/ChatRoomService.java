package com.rudolph.Weevo.chat.service;

import com.rudolph.Weevo.chat.domain.Chat;
import com.rudolph.Weevo.chat.domain.ChatRoom;
import com.rudolph.Weevo.chat.domain.enums.ChatCategory;
import com.rudolph.Weevo.chat.dto.request.ChatRoomCreateRequestDto;
import com.rudolph.Weevo.chat.dto.response.ChatMessageResponseDto;
import com.rudolph.Weevo.chat.dto.response.ChatRoomListResponseDto;
import com.rudolph.Weevo.chat.dto.summary.ChatRoomSummary;
import com.rudolph.Weevo.chat.repository.ChatRepository;
import com.rudolph.Weevo.chat.repository.ChatRoomRepository;
import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.repository.CourseRepository;
import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;

    public void createChatRoom(ChatRoomCreateRequestDto request) {
        Member sender = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Member receiver = memberRepository.findById(request.getOpponentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수신자입니다."));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        ChatCategory category;
        if (request.getCourseId() != null) {
            course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

            category = ChatCategory.valueOf(course.getCourseType().name());
        } else {
            category = ChatCategory.COFFEECHAT;
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .category(category)
                .course(course)
                .sender(sender)
                .receiver(receiver)
                .build();
        chatRoomRepository.save(chatRoom);
    }

    public ChatRoomListResponseDto getChatRooms(String category) {
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<ChatRoom> chatRoomList;

        if (category == null || category.isBlank()) {
            chatRoomList = chatRoomRepository.findByMember(member.getId());
        } else {
            ChatCategory chatCategory = ChatCategory.valueOf(category.toUpperCase());
            chatRoomList = chatRoomRepository.findByCategoryAndMember(chatCategory, member.getId());
        }

        List<ChatRoomSummary> summaries = chatRoomList.stream()
                .map(chatRoom -> {
                    Member opponent = findOpponent(chatRoom, member.getId());
                    Chat lastChat = chatRepository.findTopByChatRoomOrderBySentAtDesc(chatRoom);
                    int unreadCount = chatRepository.countByChatRoomAndReceiverIdAndIsReadFalse(chatRoom, member.getId());

                    return ChatRoomSummary.from(chatRoom, opponent, lastChat, unreadCount);
                })
                .toList();

        return ChatRoomListResponseDto.from(summaries);
    }

    private Member findOpponent(ChatRoom chatRoom, Long myId) {
        if (chatRoom.getSender().getId().equals(myId)) {
            return chatRoom.getReceiver();
        } else {
            return chatRoom.getSender();
        }
    }
}