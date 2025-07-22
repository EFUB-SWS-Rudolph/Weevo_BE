package com.rudolph.Weevo.chat.controller;

import com.rudolph.Weevo.auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.chat.dto.request.ChatMessage;
import com.rudolph.Weevo.chat.dto.request.ChatRoomCreateRequestDto;
import com.rudolph.Weevo.chat.dto.response.ChatMessageResponseDto;
import com.rudolph.Weevo.chat.dto.response.ChatRoomListResponseDto;
import com.rudolph.Weevo.chat.dto.response.ChatRoomStatusDto;
import com.rudolph.Weevo.chat.service.ChatRoomService;
import com.rudolph.Weevo.chat.service.ChatService;
import com.rudolph.Weevo.chat.service.kafka.KafkaProducer;
import com.rudolph.Weevo.memberr.domain.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final ChatRoomService chatRoomService;
    private final KafkaProducer producer;

    @MessageMapping("/message")
    public void sendMessage(ChatMessage message, Principal principal) {
        Long senderId = ((Member) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getId();
        message.setSenderId(senderId);
        producer.sendMessage(message);
    }

    // 채팅방 존재 여부
    @GetMapping("/rooms/exist")
    public ResponseEntity<ChatRoomStatusDto> checkRoomExist(@AuthenticationPrincipal CustomUserPrincipal user,
                                                            @RequestParam Long opponentId,
                                                            @RequestParam(required = false) Long courseId) {
        ChatRoomStatusDto responseDto = chatRoomService.checkChatRoomExist(user, opponentId, courseId);
        return ResponseEntity.ok(responseDto);
    }

    // 채팅방 생성
    @PostMapping("/rooms")
    public ResponseEntity<Void> createRoom(@AuthenticationPrincipal CustomUserPrincipal user,
                                           @Valid @RequestBody ChatRoomCreateRequestDto request) {
        chatRoomService.createChatRoom(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 채팅방 목록 조회
    @GetMapping("/rooms")
    public ResponseEntity<ChatRoomListResponseDto> getChatRooms(@AuthenticationPrincipal CustomUserPrincipal user,
                                                                @Valid @RequestParam(required = false) String category) {
        ChatRoomListResponseDto rooms = chatRoomService.getChatRooms(user, category);
        return ResponseEntity.ok(rooms);
    }

    // 메세지 읽음 처리
    @PatchMapping("/rooms/{chatRoomId}/read")
    public ResponseEntity<Void> markMessagesAsRead(@AuthenticationPrincipal CustomUserPrincipal user,
                                                   @PathVariable Long chatRoomId) {
        chatRoomService.markMessagesAsRead(user, chatRoomId);
        return ResponseEntity.noContent().build();
    }

    // 메세지 목록 조회
    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<ChatMessageResponseDto> getMessages(@AuthenticationPrincipal CustomUserPrincipal user,
                                                              @PathVariable Long chatRoomId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        ChatMessageResponseDto responseDto = chatService.getMessages(user, chatRoomId, page, size);
        return ResponseEntity.ok(responseDto);
    }

    // 채팅방 나가기
    @DeleteMapping("/rooms/{chatRoomId}")
    public ResponseEntity<Void> leaveChatRoom(@AuthenticationPrincipal CustomUserPrincipal user,
                                              @PathVariable Long chatRoomId){
        chatRoomService.leaveChatRoom(user, chatRoomId);
        return ResponseEntity.noContent().build();
    }
}
