package com.rudolph.Weevo.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatMessage {
    @NotNull
    private Long chatRoomId;
    @NotNull
    private Long senderId;
    @NotBlank
    private String content;
}
