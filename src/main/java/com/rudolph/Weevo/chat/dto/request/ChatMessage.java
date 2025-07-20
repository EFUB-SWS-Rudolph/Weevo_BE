package com.rudolph.Weevo.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessage {
    @NotNull
    private Long chatRoomId;
    @NotBlank
    private String content;
}
