package com.rudolph.Weevo.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomStatusDto {
    private Long chatRoomId;
    private boolean exists;

    public static ChatRoomStatusDto from(Long chatRoomId, boolean status){
        return ChatRoomStatusDto.builder()
                .chatRoomId(chatRoomId)
                .exists(status)
                .build();
    }
}
