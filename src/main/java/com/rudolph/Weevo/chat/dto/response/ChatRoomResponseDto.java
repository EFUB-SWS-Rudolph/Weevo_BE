package com.rudolph.Weevo.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomResponseDto {
    private Long chatRoomId;

    public static ChatRoomResponseDto from(Long chatRoomId){
        return ChatRoomResponseDto.builder()
                .chatRoomId(chatRoomId)
                .build();
    }
}
