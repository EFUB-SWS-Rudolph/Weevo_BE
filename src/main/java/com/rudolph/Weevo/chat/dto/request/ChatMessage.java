package com.rudolph.Weevo.chat.dto.request;

import com.rudolph.Weevo.chat.domain.Chat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessage {
    @NotNull
    private Long chatRoomId;
    @NotNull
    private Long senderId;
    @NotNull
    private Long receiverId;
    @NotBlank
    private String content;

    public static ChatMessage from (Chat chat, Long receiverId){
        return ChatMessage.builder()
                .chatRoomId(chat.getId())
                .senderId(chat.getSender().getId())
                .receiverId(receiverId)
                .content(chat.getContent())
                .build();
    }
}
