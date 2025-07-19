package com.rudolph.Weevo.chat.service;

import com.rudolph.Weevo.chat.domain.Chat;
import com.rudolph.Weevo.chat.domain.ChatRoom;
import com.rudolph.Weevo.chat.dto.request.ChatMessage;
import com.rudolph.Weevo.chat.dto.response.ChatMessageResponseDto;
import com.rudolph.Weevo.chat.repository.ChatRepository;
import com.rudolph.Weevo.chat.repository.ChatRoomRepository;
import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;

    public void saveMessage(ChatMessage message) {
        ChatRoom chatRoom = chatRoomRepository.findById(message.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

        Member sender = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(message.getContent())
                .isRead(false)
                .build();
        chatRepository.save(chat);
    }

    // 채팅방 내 메세지 읽어오기
    @Transactional(readOnly = true)
    public ChatMessageResponseDto getMessages(Long chatRoomId, int page, int size) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));
        Page<Chat> messages = chatRepository.findByChatRoomId(chatRoomId, pageable);
        List<Chat> chatMessages = messages.getContent();

        Long myId = 1L; // 실제 서비스에선 SecurityContextHolder 로 가져오기

        // 상대방 찾기: 채팅방에 저장된 sender/receiver 중 로그인한 사용자 제외
        Member opponent = chatRoom.getSender().getId().equals(myId) ? chatRoom.getReceiver() : chatRoom.getSender();

        return ChatMessageResponseDto.from(
                chatRoomId,
                opponent,
                chatRoom.getCourse(),
                chatMessages
        );
    }

    // 메세지 읽음 처리
    public void markMessagesAsRead(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!member.getId().equals(chatRoom.getSender().getId()) &&
                !member.getId().equals(chatRoom.getReceiver().getId())) {
            throw new IllegalArgumentException("채팅방에 접근할 권한이 없습니다.");
        }

        chatRepository.markAllUnreadMessagesAsRead(chatRoomId, member.getId());
    }
}
