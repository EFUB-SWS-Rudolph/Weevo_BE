package com.rudolph.Weevo.chat.service;

import com.rudolph.Weevo.auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.chat.domain.Chat;
import com.rudolph.Weevo.chat.domain.ChatRoom;
import com.rudolph.Weevo.chat.domain.enums.ChatType;
import com.rudolph.Weevo.chat.dto.request.ChatMessage;
import com.rudolph.Weevo.chat.dto.response.ChatMessageResponseDto;
import com.rudolph.Weevo.chat.repository.ChatRepository;
import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.service.MemberService;
import com.rudolph.Weevo.notification.domain.enums.NotiType;
import com.rudolph.Weevo.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final MemberService memberService;
    private final ChatRoomService chatRoomService;
    private final NotificationService notificationService;
    private final ChatRepository chatRepository;

    public void saveMessage(ChatMessage message, Principal principal) {
        Long senderId = ((CustomUserPrincipal) ((Authentication) principal).getPrincipal()).getMemberId();
        Member sender = memberService.findMember(senderId);
        Member receiver = memberService.findMember(message.getReceiverId());
        ChatRoom chatRoom = chatRoomService.findChatRoom(message.getChatRoomId());

        notificationService.createNotification(NotiType.CHAT, sender, receiver, null);

        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .type(ChatType.CHAT)
                .content(message.getContent())
                .isRead(false)
                .build();
        chatRepository.save(chat);
    }

    // 채팅방 내 메세지 읽어오기
    @Transactional(readOnly = true)
    public ChatMessageResponseDto getMessages(CustomUserPrincipal user, Long chatRoomId, int page, int size) {
        ChatRoom chatRoom = chatRoomService.findChatRoom(chatRoomId);

        Member member = memberService.findMember(user.getMemberId());
        chatRoomService.validateChatRoomAccess(chatRoom, member.getId());

        Member opponent = chatRoom.getSender().getId().equals(member.getId())
                ? chatRoom.getReceiver()
                : chatRoom.getSender();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));
        Page<Chat> messages = chatRepository.findByChatRoomId(chatRoomId, pageable);
        List<Chat> chatMessages = new ArrayList<>(messages.getContent());

        return ChatMessageResponseDto.from(
                chatRoomId,
                opponent,
                chatRoom.getCourse(),
                chatMessages
        );
    }

    public Chat findLatestByChatRoomAndSender(Long chatRoomId, Long senderId, String content) {
        return chatRepository.findFirstByChatRoomIdAndSenderIdAndContentOrderBySentAtDesc(chatRoomId, senderId, content);
    }
}
