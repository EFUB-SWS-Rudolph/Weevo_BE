package com.rudolph.Weevo.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReadMessageRequest {
    @NotNull
    private Long chatRoomId;
    @NotNull
    private Long receiverId;
}
