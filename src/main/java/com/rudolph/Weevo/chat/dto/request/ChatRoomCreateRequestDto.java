package com.rudolph.Weevo.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ChatRoomCreateRequestDto {
    @NotNull
    private Long opponentId;
    @NotNull
    private Long courseId;
    @NotBlank
    private String content;
}
