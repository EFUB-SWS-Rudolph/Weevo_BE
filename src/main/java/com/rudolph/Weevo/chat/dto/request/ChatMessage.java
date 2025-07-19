package com.rudolph.Weevo.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessage {
    private Long chatRoomId;
    private String content;
}
