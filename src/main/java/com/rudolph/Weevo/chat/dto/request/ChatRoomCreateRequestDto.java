package com.rudolph.Weevo.chat.dto.request;

import lombok.Getter;

@Getter
public class ChatRoomCreateRequestDto {
    private Long opponentId;
    private Long courseId;
    private String content;
}
