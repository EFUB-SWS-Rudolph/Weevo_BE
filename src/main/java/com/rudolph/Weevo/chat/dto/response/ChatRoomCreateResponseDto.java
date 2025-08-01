package com.rudolph.Weevo.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomCreateResponseDto {

    private Long chatRoomId;

    public static ChatRoomCreateResponseDto from(Long chatRoomId){
        return ChatRoomCreateResponseDto.builder()
                .chatRoomId(chatRoomId)
                .build();
    }
}
